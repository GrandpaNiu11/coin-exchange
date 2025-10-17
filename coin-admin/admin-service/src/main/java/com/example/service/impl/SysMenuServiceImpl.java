package com.example.service.impl;

import com.example.domin.SysRoleMenu;
import com.example.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.SysMenu;
import com.example.mapper.SysMenuMapper;
import com.example.service.SysMenuService;
@Service
public  class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuMapper sysMenuMapper;


    @Override
    public List<SysMenu> getMenusByUserId(Long userid) {
        if (sysRoleService.isSuperAmin(userid)){
            return list();
        }

        return sysMenuMapper.MenuShortcutByUserId(userid);

    }
}
