package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserAuthInfoMapper;
import com.example.domin.UserAuthInfo;
import com.example.service.UserAuthInfoService;
@Service
public class UserAuthInfoServiceImpl extends ServiceImpl<UserAuthInfoMapper, UserAuthInfo> implements UserAuthInfoService{

    @Override
    public List<UserAuthInfo> getUserAuthInfoByUserId(Long authCode) {
        return list(new LambdaQueryWrapper<UserAuthInfo>()
                .eq(UserAuthInfo::getAuthCode,authCode) //通过认证的唯一Code 来查询用户的认证信息
        );
    }

    @Override
    public List<UserAuthInfo> getUserAuthInfoByCode(Long authCode) {
        List<UserAuthInfo> list = list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getUserId, authCode));
        return  list == null ? Collections.emptyList() : list; //处理null
    }
}
