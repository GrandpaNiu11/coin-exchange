package com.example.controller;

import com.example.model.R;
import com.example.model.WebLog;
import com.example.service.TestService;
import io.swagger.annotations.*;


import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;


@RestController
@Api(tags = "测试接口")
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TestService testService;

    @GetMapping("/common/test")
    @ApiOperation(value = "测试子类", authorizations = @Authorization(value = "Authorization"))
    @ApiImplicitParams(@ApiImplicitParam(name = "a", value = "参数a", dataType = "String", paramType = "query", example = "1"))
    public R<String> test(String a) {

        return R.ok("测试成功");
    }

    @GetMapping("/common/test2")
    @ApiOperation(value = "获取时间", authorizations = @Authorization(value = "Authorization"))
    public R<Date> getTime() {
        return R.ok(new Date());
    }

    @GetMapping("/common/test3")
    @ApiOperation(value = "测试redis", authorizations = @Authorization(value = "Authorization"))
    public R<String> testRedis() {
        WebLog webLog = new WebLog();
        webLog.setIp("1212121212");
        redisTemplate.opsForValue().set("test", webLog);

        return R.ok("测试成功");
    }


    @GetMapping("/common/test4")
    @ApiOperation(value = "测试je", authorizations = @Authorization(value = "Authorization"))
    public R<String> testje() {
        testService.getWebLog("3213213214");

        return R.ok("测试成功");
    }
}
