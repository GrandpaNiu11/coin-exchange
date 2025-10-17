package com.example.match;

import com.example.disruptor.OrderEvent;
import com.example.disruptor.OrderEventHandler;
import com.example.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(value = MatchEngineProperties.class)
public class MatchEngineAutoConfiguration {


    private MatchEngineProperties matchEngineProperties;


    public MatchEngineAutoConfiguration(MatchEngineProperties matchEngineProperties) {
        this.matchEngineProperties = matchEngineProperties;
    }

    @Bean
    public EventHandler<OrderEvent>[] eventHandlers(){
        Map<String, MatchEngineProperties.CoinScale> symbols = matchEngineProperties.getSymbols();
        Set<Map.Entry<String, MatchEngineProperties.CoinScale>> entries = symbols.entrySet();
        EventHandler<OrderEvent>[] eventHandlers = new EventHandler[symbols.size()];
        int i = 0;
        for (Map.Entry<String, MatchEngineProperties.CoinScale> entry : entries) {
            String key = entry.getKey();
            MatchEngineProperties.CoinScale value = entry.getValue();
            OrderBooks orderBooks = null;
            if (value != null){
                orderBooks = new OrderBooks(key, value.getCoinScale(), value.getBaseCoinScale());
            }else {
                orderBooks = new OrderBooks(key);
            }
            eventHandlers[i++] =new OrderEventHandler(orderBooks);
        }

       return eventHandlers;
    }
}
