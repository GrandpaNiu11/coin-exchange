package com.example.disruptor;

import com.example.match.MatchServiceFactory;
import com.example.match.MatchStrategy;
import com.example.model.Order;
import com.example.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class OrderEventHandler implements EventHandler<OrderEvent> {
   int a =1;
    private OrderBooks orderBooks;

    private String symbol;

    public OrderEventHandler(OrderBooks orderBooks) {
        this.orderBooks = orderBooks;
        this.symbol = orderBooks.getSymbol();
    }
//    接收到某个消息如何消费
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {

        Order order = (Order) event.getSource();
        if (!order.getSymbol().equals(symbol)){
            return;
        }


        log.info("订单处理开始：{}",event.getSource());
        MatchServiceFactory.getMatchService(MatchStrategy.LIMIT_PRICE) .match(orderBooks,(Order) event.getSource());
        log.info("订单处理结束：{}",event.getSource());
        System.out.println("a=============================="+a);
    }
}
