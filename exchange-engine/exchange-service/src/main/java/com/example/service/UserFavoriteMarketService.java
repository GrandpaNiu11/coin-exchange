package com.example.service;

import com.example.domin.UserFavoriteMarket;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserFavoriteMarketService extends IService<UserFavoriteMarket>{


    boolean deleteUserFavoriteMarket(Long id, Long userId);
}
