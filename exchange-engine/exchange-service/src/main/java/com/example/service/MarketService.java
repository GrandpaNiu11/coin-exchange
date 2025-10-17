package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.Market;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.MarketDto;

import java.util.List;

public interface MarketService extends IService<Market>{


    Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status);

    List<Market> getMarketsByTradeAreaId(Long id);

    Market getMarketBySymbol(String symbol);

    MarketDto findByCoinId(Long buyCoinId, Long sellCoinId);

}
