package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.SysRolePrivilegeUserMapper;
import com.example.domin.SysRolePrivilegeUser;
import com.example.service.SysRolePrivilegeUserService;
@Service
public class SysRolePrivilegeUserServiceImpl extends ServiceImpl<SysRolePrivilegeUserMapper, SysRolePrivilegeUser> implements SysRolePrivilegeUserService{

}
