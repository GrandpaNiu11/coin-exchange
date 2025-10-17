package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.AdminBankDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.AdminBank;
import com.example.mapper.AdminBankMapper;
import com.example.service.AdminBankService;
import org.springframework.util.CollectionUtils;

@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank> implements AdminBankService{

    /**
     * <h2>条件查询公司银行卡</h2>
     * @param page 分页参数
     * @param bankCard  公司的银行卡
     **/
    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        return page(page, new LambdaQueryWrapper<AdminBank>()
                .like(!StringUtils.isEmpty(bankCard), AdminBank::getBankCard, bankCard)
        );
    }

    @Override
    public List<AdminBankDto> getAllAdminBanks() {
        List<AdminBank> adminBanks = list(new LambdaQueryWrapper<AdminBank>().eq(AdminBank::getStatus, 1));
        if (CollectionUtils.isEmpty(adminBanks)){
            return Collections.emptyList();
        }
        // adminBank -> adminBankDto 实体转换

        List<AdminBankDto> adminBankDtos = new ArrayList<>() ;

        for (AdminBank adminBank : adminBanks) {
            AdminBankDto dto = new AdminBankDto();
            BeanUtils.copyProperties(adminBank, dto);
            adminBankDtos.add(dto);
        }
        return adminBankDtos;
    }

}
