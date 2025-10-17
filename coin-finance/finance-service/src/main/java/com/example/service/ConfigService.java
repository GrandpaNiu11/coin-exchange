package com.example.service;

import com.example.domin.Config;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ConfigService extends IService<Config>{


    Config getConfigByCode(String fail);
}
