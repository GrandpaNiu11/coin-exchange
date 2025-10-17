package com.example.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domin.SysMenu;
import com.example.domin.SysPrivilege;
import com.example.model.RolePrivilegesParam;
import com.example.service.SysMenuService;
import com.example.service.SysPrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.SysRolePrivilegeMapper;
import com.example.domin.SysRolePrivilege;
import com.example.service.SysRolePrivilegeService;
@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege> implements SysRolePrivilegeService{

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    @Override
    public List<SysMenu> findSysMenuAndPrivileges(long roleId) {
        //查询所有的菜单
        List<SysMenu> menus = sysMenuService.list();
        //在页面显示的是二级菜单，以及二级菜单包含的权限
        //一级菜单
        List<SysMenu> oneMenus = menus.stream()
                .filter(sysMenu -> sysMenu.getParentId() == null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(oneMenus)) {
            return Collections.emptyList();
        }
        List<SysMenu> twoMenus = new ArrayList<>();
        oneMenus.forEach(menu -> {
            twoMenus.addAll(getChildMenus(menu.getId(), roleId, menus));
        });
        return twoMenus;
    }

    @Override
    public boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam) {
        Long roleId = rolePrivilegesParam.getRoleId();
        //删除该角色之前的权限数据
        sysRolePrivilegeService.remove(new LambdaQueryWrapper<SysRolePrivilege>().eq(SysRolePrivilege::getRoleId, roleId));
        //删除成功再新增数据
        List<Long> privilegeIds = rolePrivilegesParam.getPrivilegeIds();
        if (!CollectionUtils.isEmpty(privilegeIds)) {
            List<SysRolePrivilege> rolePrivileges = new ArrayList<>();
            privilegeIds.forEach(privilegeId -> {
                SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                sysRolePrivilege.setRoleId(roleId);
                sysRolePrivilege.setPrivilegeId(privilegeId);
                rolePrivileges.add(sysRolePrivilege);
            });
            //批量保存
            return sysRolePrivilegeService.saveBatch(rolePrivileges);
        }
        return true;
    }

    private List<SysMenu> getChildMenus(Long parentId, long roleId, List<SysMenu> sources) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : sources) {
            if (Objects.equals(parentId, menu.getParentId())) {
                children.add(menu);
                menu.setChilds(getChildMenus(menu.getId(), roleId, sources));
                //给菜单添加权限
                List<SysPrivilege> privileges = sysPrivilegeService.getAllSysPrivilegesByRoleId(menu.getId(), roleId);
                menu.setPrivileges(privileges);
            }
        }
        return children;
    }
}
