package com.example.service;

import com.example.model.LoginForm;
import com.example.model.LoginUser;

public interface LoginService {

    /*
     *  会员的登录
     * */
    LoginUser login(LoginForm loginForm);
}
