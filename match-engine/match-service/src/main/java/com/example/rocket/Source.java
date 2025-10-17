package com.example.rocket;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Source {
    //盘口数据输出
    @Output("trade_plate_out")
    public MessageChannel plateOut();

    //完成订单数据的输出
    @Output("completed_orders_out")
    public MessageChannel comPletedOrdersOut();

    //交易记录的输出
    @Output("exchange_trades_out")
    public MessageChannel exchangeTradesOut();
}
