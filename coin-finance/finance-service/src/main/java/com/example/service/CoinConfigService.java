package com.example.service;

import com.example.domin.CoinConfig;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinConfigService extends IService<CoinConfig>{


    CoinConfig findByCoinId(Long coinId);

    boolean updateOrSave(CoinConfig coinConfig);
}
