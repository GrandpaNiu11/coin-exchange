package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CoinTypeMapper;
import com.example.domin.CoinType;
import com.example.service.CoinTypeService;
@Service
public class CoinTypeServiceImpl extends ServiceImpl<CoinTypeMapper, CoinType> implements CoinTypeService{


    /*
     *  条件分页查询货币类型
     * */
    @Override
    public Page<CoinType> findByPage(Page<CoinType> page, String code) {
        return page(page,new LambdaQueryWrapper<CoinType>()
                .like(!StringUtils.isEmpty(code), CoinType::getCode,code));
    }

    @Override
    public List<CoinType> listByStatus(Byte status) {


        return list(new LambdaQueryWrapper<CoinType>().eq(status!=null,CoinType::getStatus,status));



    }


}
