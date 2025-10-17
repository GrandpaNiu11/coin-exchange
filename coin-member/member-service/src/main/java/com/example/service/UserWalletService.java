package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.UserWallet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserWalletService extends IService<UserWallet>{


    Page<UserWallet> findByPage(Page<UserWallet> page, Long userId);

    List<UserWallet> findUserWallets(Long userId, Long coinId);

    boolean add(Long userId, UserWallet userWallet);

    boolean delete(Long addressId, String payPassword);
}
