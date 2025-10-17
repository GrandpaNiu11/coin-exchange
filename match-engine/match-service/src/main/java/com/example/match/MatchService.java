package com.example.match;

import com.example.model.Order;
import com.example.model.OrderBooks;

public interface MatchService {
    void match(OrderBooks orderBooks, Order order);
}
