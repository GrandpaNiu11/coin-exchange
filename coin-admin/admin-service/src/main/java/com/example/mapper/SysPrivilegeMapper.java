package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domin.SysPrivilege;

import java.util.Set;

public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {

    Set<Long> getPrivilegesByRoleId(long roleId);
}