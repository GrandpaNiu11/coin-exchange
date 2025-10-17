package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.CoinType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinTypeService extends IService<CoinType>{


    Page<CoinType> findByPage(Page<CoinType> page, String code);

    List<CoinType> listByStatus(Byte status);

}
