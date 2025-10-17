package com.example.controller;

import com.example.model.LoginResult;
import com.example.service.SysLoginService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("/login")
    @ApiOperation(value = "后台管理人员登录")
    public LoginResult login(@RequestParam(required = true)String username , @RequestParam(required = true) String password ) {
        return sysLoginService.login(username,password);

    }
}
