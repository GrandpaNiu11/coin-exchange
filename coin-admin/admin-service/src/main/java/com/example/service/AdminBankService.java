package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.AdminBankDto;

import java.util.List;

public interface AdminBankService extends IService<AdminBank>{


    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);

    List<AdminBankDto> getAllAdminBanks();


}
