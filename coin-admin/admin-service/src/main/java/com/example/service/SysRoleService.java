package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysRoleService extends IService<SysRole>{


    boolean isSuperAmin(Long string);

    Page<SysRole> findByPage(Page<SysRole> page, String name);
}
