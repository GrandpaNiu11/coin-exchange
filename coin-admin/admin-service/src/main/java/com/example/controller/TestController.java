package com.example.controller;

import com.example.domin.SysUser;
import com.example.model.R;
import com.example.service.SysUserService;
import com.example.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "后台管理接口")
public class TestController {
     @Autowired
     private SysUserService sysUserService;

    @ApiOperation(value = "查询用户详情")
    @GetMapping("/user/detail/{id}")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    public R<SysUser> getsysuuserInfo(@PathVariable("id") Long id) {
        SysUser byId = sysUserService.getById(id);

        return R.ok(byId);
    }
}
