package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.TurnoverOrder;
import com.example.mapper.TurnoverOrderMapper;
import com.example.service.TurnoverOrderService;
@Service
public class TurnoverOrderServiceImpl extends ServiceImpl<TurnoverOrderMapper, TurnoverOrder> implements TurnoverOrderService{

    @Override
    public Page<TurnoverOrder> findByPage(Page<TurnoverOrder> page, Long userId, String symbol, Integer type) {
        LambdaQueryWrapper<TurnoverOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TurnoverOrder::getBuyUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type)
                .or(wrapper -> wrapper.eq(TurnoverOrder::getSellUserId,userId)
                        .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                        .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type));
/*        lambdaQueryWrapper.eq(TurnoverOrder::getBuyUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type);*/
/*        lambdaQueryWrapper.eq(TurnoverOrder::getSellUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type);*/
        return page(page,lambdaQueryWrapper);
    }

    @Override
    public List<TurnoverOrder> getBuyTurnoverOrder(Long id, Long userId) {
        return list(new LambdaQueryWrapper<TurnoverOrder>().eq(TurnoverOrder::getOrderId, id)
                .eq(TurnoverOrder::getBuyUserId, userId)
        );
    }

    @Override
    public List<TurnoverOrder> getSellTurnoverOrder(Long id, Long userId) {
        return list(new LambdaQueryWrapper<TurnoverOrder>().eq(TurnoverOrder::getOrderId, id)
                .eq(TurnoverOrder::getSellUserId, userId)
        );
    }

    @Override
    public List<TurnoverOrder> findBySymbol(String symbol) {
        List<TurnoverOrder> turnoverOrders = list(
                new LambdaQueryWrapper<TurnoverOrder>()
                        .eq(TurnoverOrder::getSymbol, symbol)
                        .orderByDesc(TurnoverOrder::getCreated)
                        .eq(TurnoverOrder::getStatus,1)
                        .last("limit 60")
        );
        return turnoverOrders;
    }
}
