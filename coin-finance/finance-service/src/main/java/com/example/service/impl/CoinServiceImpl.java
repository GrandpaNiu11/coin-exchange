package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdminBankDto;
import com.example.dto.CoinDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.Coin;
import com.example.mapper.CoinMapper;
import com.example.service.CoinService;
import org.springframework.util.CollectionUtils;

@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService{

    @Override
    public Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page) {
        return page(page,
                new LambdaQueryWrapper<Coin>()
                        .like(!StringUtils.isEmpty(name),Coin::getName,name) // 名称的模糊查询
                        .like(!StringUtils.isEmpty(title),Coin::getTitle,title) // 标题的模糊查询
                        .eq(status!= null,Coin::getStatus,status)   // 状态的查询
                        .eq(!StringUtils.isEmpty(type),Coin::getType,title) // 货币类型名称的查询
                        .eq(!StringUtils.isEmpty(walletType),Coin::getWallet,walletType)    //货币钱包类型的查询
        );
    }

    @Override
    public List<Coin> getCoinsByStatus(Byte status) {

        return list(new LambdaQueryWrapper<Coin>().eq(Coin::getStatus,status));
    }

    @Override
    public Coin getCoinByCoinName(String coinName) {
        return getOne(new LambdaQueryWrapper<Coin>().eq(Coin::getName,coinName));
    }

    /*
     *  使用coinId的id集合,查询我们的币种
     * */
    @Override
    public List<CoinDto> findList(List<Long> coinIds) {
        List<Coin> coins = super.listByIds(coinIds);
        if (CollectionUtils.isEmpty(coins)){
            return Collections.emptyList();
        }
        List<CoinDto> coinDtos =new ArrayList<>();
        for (Coin adminBank : coins) {
            CoinDto dto = new CoinDto();
            BeanUtils.copyProperties(adminBank, dto);
            coinDtos.add(dto);
        }
        return coinDtos;
    }
}
