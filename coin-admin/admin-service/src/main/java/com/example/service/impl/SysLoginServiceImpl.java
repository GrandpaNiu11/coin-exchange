package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.example.domin.SysMenu;
import com.example.feign.JwtToken;
import com.example.feign.OAuth2FeignClient;
import com.example.model.LoginResult;
import com.example.service.SysLoginService;
import com.example.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {
    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public LoginResult login(String username, String password) {

        ResponseEntity<JwtToken> userInfo = oAuth2FeignClient.getUserInfo("password", username, password, "admin_type", "Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=");
        if (userInfo.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ApiErrorCode.FAILED);
        }

        JwtToken body = userInfo.getBody();
        log.info("远程服务调用成功登录成功 body: " + body);
        String accessToken = body.getAccessToken();

        Jwt jwt = JwtHelper.decode(accessToken);
        String jwtJsonstr = jwt.getClaims();
        JSONObject jwtJson = JSON.parseObject(jwtJsonstr);
        Object userid = jwtJson.get("user_name");
        List<SysMenu> menus = sysMenuService.getMenusByUserId(Long.valueOf( userid.toString()));

        JSONArray authorities = jwtJson.getJSONArray("authorities");
        List<SimpleGrantedAuthority> collect = authorities.stream().map(item ->
                new SimpleGrantedAuthority(item.toString()))
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(  accessToken,"", Long.parseLong(body.getExpiresIn()), java.util.concurrent.TimeUnit.SECONDS);


        return new  LoginResult(accessToken, menus, collect);
    }
}
