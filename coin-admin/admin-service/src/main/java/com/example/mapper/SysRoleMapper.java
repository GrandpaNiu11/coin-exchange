package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domin.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    String getUserRoleCode(Long userid);

}