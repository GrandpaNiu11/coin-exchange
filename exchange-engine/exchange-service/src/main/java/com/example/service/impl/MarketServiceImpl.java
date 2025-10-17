package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.CoinDto;
import com.example.dto.MarketDto;
import com.example.feign.CoinServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.MarketMapper;
import com.example.domin.Market;
import com.example.service.MarketService;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService{
    @Autowired
    private CoinServiceFeign coinServiceFeign;
    /*
     *  分页查询市场的配置
     * */
    @Override
    public Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status) {
        return page(page,new LambdaQueryWrapper<Market>()
                .eq(tradeAreaId!=null,Market::getTradeAreaId,tradeAreaId)
                .eq(status!=null,Market::getStatus,status));
    }

    @Override
    public List<Market> getMarketsByTradeAreaId(Long id) {
        return list(new LambdaQueryWrapper<Market>()
                .eq(Market::getTradeAreaId,id)
                .eq(Market::getStatus,1)
                .orderByAsc(Market::getSort));
    }

    @Override
    public Market getMarketBySymbol(String symbol) {
        return getOne(new LambdaQueryWrapper<Market>()
                .eq(Market::getSymbol,symbol));
    }

    @Override
    public MarketDto findByCoinId(Long buyCoinId, Long sellCoinId) {
        LambdaQueryWrapper<Market> queryWrapper = new LambdaQueryWrapper<Market>()
                .eq(Market::getBuyCoinId, buyCoinId)
                .eq(Market::getSellCoinId, sellCoinId)
                .eq(Market::getStatus, 1);
        Market one = getOne(queryWrapper);
        if (one == null){
            return null;
        }
        MarketDto marketDto = new MarketDto();
        BeanUtils.copyProperties(one, marketDto);
        return marketDto;
    }

    /*
     *  重写save方法
     * */
    @Override
    public boolean save(Market market){
        log.info("开始新增市场数据{}", JSON.toJSONString(market));
        Long sellCoinId = market.getSellCoinId(); // 报价货币
        Long buyCoinId = market.getBuyCoinId(); //基础货币
        List<CoinDto> coins = coinServiceFeign.findCoins(Arrays.asList(sellCoinId, buyCoinId));
        if (CollectionUtils.isEmpty(coins) || coins.size() != 2){
            throw new IllegalArgumentException("货币输入错误");
        }
        CoinDto coinDto = coins.get(0);
        CoinDto sellCoin = null;
        CoinDto buyCoin = null;
        if (coinDto.getId().equals(sellCoinId)){
            sellCoin = coinDto;
            buyCoin = coins.get(1);
        }else {
            sellCoin = coins.get(1);
            buyCoin = coinDto;
        }

        market.setName(sellCoin.getName() + "/" + buyCoin.getName()); //交易市场的名称 -> 报价货币/基础货币
        market.setTitle(sellCoin.getTitle() + "/" + buyCoin.getTitle()); //交易市场的标题  -> 报价货币/基础货币
        market.setSymbol(sellCoin.getName() + buyCoin.getName() ); //交易市场的标题-> 报价货币基础货币
        market.setImg(sellCoin.getImg());  // 交易市场的标题
        return super.save(market);
    }

}
