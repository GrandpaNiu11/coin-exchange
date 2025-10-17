package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserAddressService extends IService<UserAddress>{


    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);

    UserAddress getUserAddressByUserIdAndCoinId(Long coinId, Long userId);
}
