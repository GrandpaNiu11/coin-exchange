package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.Coin;
import com.example.service.CoinService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.AdminAddressMapper;
import com.example.domin.AdminAddress;
import com.example.service.AdminAddressService;
@Service
public class AdminAddressServiceImpl extends ServiceImpl<AdminAddressMapper, AdminAddress> implements AdminAddressService{
    @Autowired
    private CoinService coinService;
    @Override
    public Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId) {
        return page(page,new LambdaQueryWrapper<AdminAddress>().eq(coinId!=null,AdminAddress::getCoinId,coinId));
    }

    /*
     *  重写save方法，为了让我们的归集地址里面包含coinType
     * */
    @Override
    public boolean save(AdminAddress entity) {
        Long coinId = entity.getCoinId();
        Coin coin = coinService.getById(coinId);
        if (coin == null){
            throw new IllegalArgumentException("输入的币种id错误");
        }
        String type = coin.getType();
        entity.setCoinType(type);
        return super.save(entity);
    }
}
