package com.example.model;

import com.example.enums.OrderDirection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
@Data
@Slf4j
public class OrderBooks {
    /**
     * 买入的限价交易 价格从高到底
     * eg: 价格越高，越容易买到
     * Key: 价格
     * MergeOrder 同价格的订单，订单按照时间排序
     */
    private TreeMap<BigDecimal, MergeOrder> buyLimitPrice;

    /**
     * 卖出的限价交易，价格从低到高
     * eg: 价格越低，卖出的越容易
     */
    private TreeMap<BigDecimal, MergeOrder> sellLimitPrice;
    /**
     * 交易的币种
     */
    private String symbol;

    /**
     * 交易币种的精度
     */
    private int coinScale;

    /**
     * 基币的精度
     */
    private int baseCoinScale;
    /**
     * 买方的盘口数据
     */
    private TradePlate buyTradePlate;

    /**
     * 卖方的盘口数据
     */
    private TradePlate sellTradePlate;

    /**
     * 日期格式器
     */
    private SimpleDateFormat dateTimeFormat;

    public OrderBooks(String symbol) {
        this(symbol, 4, 4);
    }

    public OrderBooks(String symbol, int coinScale, int baseCoinScale) {
        this.symbol = symbol;
        this.coinScale = coinScale;
        this.baseCoinScale = baseCoinScale;
        this.initialize();
    }


    /**
     * 初始化队列
     */
    public void initialize() {
        log.info("init CoinTrader for symbol {}", symbol);
        // 载入比较器(排序树)
        buyLimitPrice = new TreeMap<>(Comparator.reverseOrder()); //价格从大到小

        sellLimitPrice = new TreeMap<>(Comparator.naturalOrder()); // 价格从小到大

        buyTradePlate = new TradePlate(symbol, OrderDirection.BUY);

        sellTradePlate = new TradePlate(symbol, OrderDirection.SELL);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

//    获取当前的交易队列
    public  TreeMap<BigDecimal, MergeOrder> getCurrentLimitPrices(OrderDirection direction){
        return direction == OrderDirection.BUY ? buyLimitPrice : sellLimitPrice;
    }
//   获取当前的交易的队列迭代器
    public  Iterator<Map.Entry<BigDecimal, MergeOrder>> getCurrentLimitPricesIterator(OrderDirection direction){
        return getCurrentLimitPrices(direction).entrySet().iterator();
    }
    //怎末添加一个订单进入我们队列里面
    public void addOrder(Order order) {
        TreeMap<BigDecimal, MergeOrder> currentLimitPrices = getCurrentLimitPrices(OrderDirection.getOrderDirection(order.getOrderDirection().getCode()));
        MergeOrder mergeOrder = currentLimitPrices.get(order.getPrice());
        if (mergeOrder == null){
            mergeOrder = new MergeOrder();
            currentLimitPrices.put(order.getPrice(), mergeOrder);
        }
        mergeOrder.add(order);
        // 将新的订单添加到盘口里面
        if(order.getOrderDirection()==OrderDirection.BUY){
            buyTradePlate.add(order);
        }else{
            sellTradePlate.add(order);
        }
    }

    //取消一个订单
    public void cancelOrder(Order order) {
        TreeMap<BigDecimal, MergeOrder> currentLimitPrices = getCurrentLimitPrices(OrderDirection.getOrderDirection(order.getOrderDirection().getCode()));
        MergeOrder mergeOrder = currentLimitPrices.get(order.getPrice());
        if (mergeOrder != null){
            return;
        }
        Iterator<Order> iterator = mergeOrder.iterator();
        while (iterator.hasNext()){
            Order next = iterator.next();
            if (next.getOrderId().equals(order.getOrderId())){
                iterator.remove();
            }
        }
        if (mergeOrder.size() == 0){
            currentLimitPrices.remove(order.getPrice());
        }


    }
    //获取排在队列里面的第一个数据
    public Map.Entry<BigDecimal, MergeOrder> getFirstOrder(OrderDirection direction){
        return getCurrentLimitPrices(direction).firstEntry();
    }




}
