package com.example.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.SysPrivilege;
import com.example.mapper.SysPrivilegeMapper;
import com.example.service.SysPrivilegeService;
@Service
public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege> implements SysPrivilegeService{
    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;


    @Override
    public List<SysPrivilege> getAllSysPrivilegesByRoleId(Long menuId, long roleId) {
        //查询该菜单下的所有权限
        List<SysPrivilege> privileges = list(new LambdaQueryWrapper<SysPrivilege>().eq(SysPrivilege::getMenuId, menuId));
        if (CollectionUtils.isEmpty(privileges)) {
            return Collections.emptyList();
        }
        Set<Long> privilegesOfRole = sysPrivilegeMapper.getPrivilegesByRoleId(roleId);
        if (!CollectionUtils.isEmpty(privilegesOfRole)) {
            for (SysPrivilege privilege : privileges) {
                if (privilegesOfRole.contains(privilege.getId())) {
                    privilege.setOwn(1);
                }
            }
        }
        return privileges;
    }
}
