package com.example.controller;

import com.example.disruptor.OrderEvent;
import com.example.disruptor.OrderEventHandler;
import com.example.domain.DepthItemVo;
import com.example.enums.OrderDirection;
import com.example.feign.OrderBooksFeignClient;
import com.example.model.MergeOrder;
import com.example.model.OrderBooks;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class MatchController implements OrderBooksFeignClient {

    @Autowired
    private EventHandler<OrderEvent>[] eventHandlers;

    @Override
    public Map<String, List<DepthItemVo>> querySymbolDepth(String symbol) {
        for (EventHandler<OrderEvent> eventHandler : eventHandlers) {
            OrderEventHandler orderEventHandler=(OrderEventHandler) eventHandler;
            if (orderEventHandler.getSymbol().equals(symbol)){
                HashMap<String, List<DepthItemVo>> deptMap = new HashMap<>();
                deptMap.put("asks",orderEventHandler.getOrderBooks().getSellTradePlate().getItems());
                deptMap.put("bids",orderEventHandler.getOrderBooks().getBuyTradePlate().getItems());
            return deptMap;
            }
        }
        return null;
    }

   @GetMapping("/match/order")
    public TreeMap<BigDecimal, MergeOrder> queryOrderBooks(@RequestParam(required = true) String symbol,Integer orderDirection) {
        for (EventHandler<OrderEvent> eventHandler : eventHandlers) {
            OrderEventHandler orderEventHandler = (OrderEventHandler) eventHandler;
            if(orderEventHandler.getSymbol().equals(symbol)){
                OrderBooks orderBooks =
                        orderEventHandler.getOrderBooks();
                return  orderBooks.getCurrentLimitPrices(OrderDirection.getOrderDirection(orderDirection));
            }
        }
        return null;
    }



}
