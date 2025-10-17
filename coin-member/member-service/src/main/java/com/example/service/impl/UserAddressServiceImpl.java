package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserAddressMapper;
import com.example.domin.UserAddress;
import com.example.service.UserAddressService;
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService{

    @Override
    public Page<UserAddress> findByPage(Page<UserAddress> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId));
    }

    @Override
    public UserAddress getUserAddressByUserIdAndCoinId(Long coinId, Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getCoinId,coinId).eq(UserAddress::getUserId,userId);
        return getOne(queryWrapper);
    }
}
