package com.example.service;

import com.example.domin.UserAuthInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthInfoService extends IService<UserAuthInfo>{


    List<UserAuthInfo> getUserAuthInfoByUserId(Long id);

    List<UserAuthInfo> getUserAuthInfoByCode(Long authCode);
}
