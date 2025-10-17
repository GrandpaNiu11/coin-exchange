package com.example.service;

import com.example.domin.SysPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPrivilegeService extends IService<SysPrivilege>{


    List<SysPrivilege> getAllSysPrivilegesByRoleId(Long id, long roleId);
}
