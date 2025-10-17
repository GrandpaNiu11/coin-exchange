package com.example.service;

import com.example.model.LoginResult;

public interface SysLoginService {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    LoginResult login(String username, String password);
}
