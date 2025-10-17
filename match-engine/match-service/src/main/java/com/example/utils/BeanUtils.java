package com.example.utils;

import com.example.domin.EntrustOrder;
import com.example.enums.OrderDirection;
import com.example.model.Order;

public class BeanUtils {
    public static Order entrustOrder20rder(EntrustOrder entrustOrder) {
        Order order = new Order();
        order.setOrderId(entrustOrder.getId().toString());
        order.setPrice(entrustOrder.getPrice());
        order.setAmount(entrustOrder.getVolume().add(entrustOrder.getDeal().negate()));

        order.setSymbol(entrustOrder.getSymbol());
        order.setTime(entrustOrder.getCreated().getTime());
        order.setOrderDirection( OrderDirection.getOrderDirection(entrustOrder.getType()));

        return order;
    }
}
