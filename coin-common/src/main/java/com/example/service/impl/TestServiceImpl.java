package com.example.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.example.model.WebLog;
import com.example.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {


    @Override
    @Cached(name = "com.example.service.impl.TestServiceImpl" ,key = "#username", cacheType = CacheType.REMOTE )
    public WebLog getWebLog(String username) {
        WebLog webLog = new WebLog();
        webLog.setIp("1212121212");
        webLog.setSpendTime(1000);
        return webLog;
    }

}
