package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.TradeArea;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.TradeAreaMarketVo;

import java.util.List;

public interface TradeAreaService extends IService<TradeArea>{


    Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status);

    List<TradeArea> findAll(Byte status);

    List<TradeAreaMarketVo> findTradeAreaMarket();

    List<TradeAreaMarketVo> getUserFavoriteMarkets(Long userId);
}
