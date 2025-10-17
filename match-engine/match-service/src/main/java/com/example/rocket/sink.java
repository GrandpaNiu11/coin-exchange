package com.example.rocket;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface sink {

    @Input("order_in")
    public MessageChannel messageChannel();
}
