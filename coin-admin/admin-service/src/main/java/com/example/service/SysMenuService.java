package com.example.service;

import com.example.domin.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{


    List<SysMenu> getMenusByUserId(Long string);
}
