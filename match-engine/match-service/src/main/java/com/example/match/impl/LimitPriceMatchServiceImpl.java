package com.example.match.impl;

import com.example.domain.ExchangeTrade;
import com.example.enums.OrderDirection;
import com.example.match.MatchService;
import com.example.match.MatchServiceFactory;
import com.example.match.MatchStrategy;
import com.example.model.MergeOrder;
import com.example.model.Order;
import com.example.model.OrderBooks;
import com.example.model.TradePlate;
import com.example.rocket.Source;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LimitPriceMatchServiceImpl implements MatchService, InitializingBean {
    @Autowired
    private Source source;

    @Override
    public void match(OrderBooks orderBooks, Order order) {


        log.info("开始执行撮合");
        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Iterator<Map.Entry<BigDecimal, MergeOrder>> markerQueueIterator = null;
        //2 获取一个挂单队列
        if (order.getOrderDirection() == OrderDirection.BUY) {

            markerQueueIterator = orderBooks.getCurrentLimitPricesIterator(OrderDirection.SELL);
        } else {

            markerQueueIterator = orderBooks.getCurrentLimitPricesIterator(OrderDirection.BUY);
        }

        ArrayList<Order> completedOrders = new ArrayList<>();
//        交易记录
        ArrayList<ExchangeTrade> exchangeTrades = new ArrayList<>();
        Boolean exitLoop = false;
        while (markerQueueIterator.hasNext() && !exitLoop) {
            Map.Entry<BigDecimal, MergeOrder> markerOrderEntry = markerQueueIterator.next();
            BigDecimal markerPrice = markerOrderEntry.getKey();
            MergeOrder mergeOrder = markerOrderEntry.getValue();
            if (order.getOrderDirection() == OrderDirection.BUY && order.getPrice().compareTo(markerPrice) < 0) {
                break;
            }
            if (order.getOrderDirection() == OrderDirection.SELL && order.getPrice().compareTo(markerPrice) > 0) {
                break;
            }

            Iterator<Order> mergeOrderIterator = mergeOrder.iterator();
            while (mergeOrderIterator.hasNext()) {

                Order marker = mergeOrderIterator.next();
                ExchangeTrade trade = processMath(order, marker, orderBooks);
                exchangeTrades.add(trade);
                if (order.isCompleted()) {
                    exitLoop = true;
                    completedOrders.add(order);
                    break;
                }
                if (marker.isCompleted()) {
                    mergeOrderIterator.remove();
                    completedOrders.add(marker);
                }


            }
            if (mergeOrder.size() == 0) {
                markerQueueIterator.remove();
            }

        }
//        若我们订单没有完成
        if (order.getAmount().compareTo(order.getTradedAmount())>0){
            orderBooks.addOrder(order);
        }
//        发送交易记录
        if (exchangeTrades.size()>0){
            hadlerExchangeTrades(exchangeTrades);
        }

        if (completedOrders.size() > 0) {
//            发送已经成交的交易记录
            completedOrders(completedOrders);
            //发送盘口数据,更新盘口
            TradePlate tradePlate = order.getOrderDirection() == OrderDirection.BUY ?
                    orderBooks.getBuyTradePlate() : orderBooks.getSellTradePlate();





//            发送盘口数据
            sendTradePlateData(tradePlate);
        }



    }

    private ExchangeTrade processMath(Order taker, Order marker,OrderBooks orderBooks) {
        //定义交易变量
        //成交的价格
        BigDecimal dealPrice = marker.getPrice();
        //成交的数量
        BigDecimal turnoverAmount = BigDecimal.ZERO;
        //本次需要的数量
        BigDecimal needAmount = calcTradeAmount(taker);
        //本次提供给你的数量
        BigDecimal ProviderAmount =  calcTradeAmount(marker);

        turnoverAmount = needAmount.compareTo(ProviderAmount) <= 0 ? needAmount : ProviderAmount;

        if (turnoverAmount.compareTo(BigDecimal.ZERO)== 0){
            return null;
        }


        // 设置本次吃单的成交数据
        taker.setTradedAmount(taker.getTradedAmount().add(turnoverAmount));
        BigDecimal turnoverTaker = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getCoinScale(), RoundingMode.HALF_UP);
        taker.setTurnover(turnoverTaker);

        // 设置本次挂单的成交数据
        marker.setTradedAmount(marker.getTradedAmount().add(turnoverAmount));
        BigDecimal markerTurnover = turnoverAmount.multiply(dealPrice).setScale(orderBooks.getBaseCoinScale(), RoundingMode.HALF_UP);
        marker.setTurnover(markerTurnover);

        // 生成交易记录
        ExchangeTrade exchangeTrade = new ExchangeTrade();

        exchangeTrade.setAmount(turnoverAmount); // 设置购买的数量
        exchangeTrade.setPrice(dealPrice);  // 设置购买的价格
        exchangeTrade.setTime(System.currentTimeMillis()); // 设置成交的时间
        exchangeTrade.setSymbol(orderBooks.getSymbol());  // 设置成交的交易对
        exchangeTrade.setDirection(taker.getOrderDirection());  // 设置交易的方法
        exchangeTrade.setSellOrderId(marker.getOrderId()); // 设置出售方的id
        exchangeTrade.setBuyOrderId(taker.getOrderId()); // 设置买方的id

        exchangeTrade.setBuyTurnover(taker.getTurnover()); // 设置买方的交易额
        exchangeTrade.setSellTurnover(marker.getTurnover()); // 设置卖方的交易额

        if (taker.getOrderDirection() == OrderDirection.BUY){
            orderBooks.getBuyTradePlate().remove( marker,turnoverAmount);
        }else {
            orderBooks.getSellTradePlate().remove( marker,turnoverAmount);
        }
        return exchangeTrade;
    }

    private BigDecimal calcTradeAmount(Order order) {
        /*
         *  本来的数量-交易后的数量(已经成交数量)
         * */
        return order.getAmount().subtract(order.getTradedAmount());
    }

    private void hadlerExchangeTrades(List<ExchangeTrade> exchangeTrades) {

        /*
         *  向RocketMQ发送订单记录
         * */
        Message<List<ExchangeTrade>> message = MessageBuilder
                .withPayload(exchangeTrades)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.exchangeTradesOut().send(message);
        log.info("本次成交的记录为:" + exchangeTrades);
        log.info("本次成交的记录为:" + exchangeTrades);
    }

    private void completedOrders(List<Order> completedOrders) {
        /*
         *  向RocketMQ发送订单数据
         * */
        Message<List<Order>> message = MessageBuilder
                .withPayload(completedOrders)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.comPletedOrdersOut().send(message);

    }

    private void sendTradePlateData(TradePlate tradePlate) {
        /*
         *  向RocketMQ发送盘口数据
         * */
        Message<TradePlate> message = MessageBuilder
                .withPayload(tradePlate)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();
        source.plateOut().send(message);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        MatchServiceFactory.addMatchService(MatchStrategy.LIMIT_PRICE, this);
    }
}
