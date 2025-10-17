package com.example.rocket;

import com.example.disruptor.DisruptorTemplate;
import com.example.domin.EntrustOrder;
import com.example.model.Order;
import com.example.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumerListener {

    @Autowired
    private DisruptorTemplate disruptorTemplate;


    @StreamListener("order_in")
    public void onMessage(EntrustOrder order) {
        log.info("接收到委托单：{}",order);
        Order order1 = BeanUtils.entrustOrder20rder(order);
        disruptorTemplate.onData(order1);
    }

}
