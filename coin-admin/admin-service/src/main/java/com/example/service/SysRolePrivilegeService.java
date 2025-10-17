package com.example.service;

import com.example.domin.SysMenu;
import com.example.domin.SysRolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.RolePrivilegesParam;

import java.util.List;

public interface SysRolePrivilegeService extends IService<SysRolePrivilege>{


    List<SysMenu> findSysMenuAndPrivileges(long roleId);

    boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam);

}
