package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.AdminAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AdminAddressService extends IService<AdminAddress>{


    Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId);
}
