package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.EntrustOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.param.OrderParam;
import com.example.vo.TradeEntrustOrderVo;

public interface EntrustOrderService extends IService<EntrustOrder>{


    Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type);

    Page<TradeEntrustOrderVo> getEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    Page<TradeEntrustOrderVo> getHistoryEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    Boolean createEntrustOrder(Long userId, OrderParam orderParam);
}
