package com.example.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.SysUserRole;
import com.example.service.SysUserRoleService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.SysUser;
import com.example.mapper.SysUserMapper;
import com.example.service.SysUserService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * <h2>分页条件查询员工</h2>
     * @param page 分页数据
     * @param mobile 手机号
     * @param fullname 员工全称
     **/
    @Override
    public Page<SysUser> findByPage(Page<SysUser> page, String mobile, String fullname) {
        Page<SysUser> userPage = page(page, new LambdaQueryWrapper<SysUser>()
                .like(!StringUtils.isEmpty(mobile), SysUser::getMobile, mobile)
                .like(!StringUtils.isEmpty(fullname), SysUser::getFullname, fullname)
        );
        List<SysUser> sysUserList = userPage.getRecords();
        for (SysUser sysUser : sysUserList) {
            List<SysUserRole> userRoleList = sysUserRoleService.list(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, sysUser.getId())
            );
            if (!CollectionUtils.isEmpty(userRoleList)) {
                sysUser.setRole_strings(
                        userRoleList.stream()
                                .map(sysUserRole -> sysUserRole.getRoleId().toString())
                                .collect(Collectors.joining(","))
                );
            }
        }
        return userPage;
    }

    /**
     * <h2>新增员工</h2>
     * @param sysUser
     **/
    @Transactional
    @Override
    public boolean addUser(SysUser sysUser) {
        String roleStrings = sysUser.getRole_strings();
        //给密码加密
        String password = sysUser.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        sysUser.setPassword(encoder.encode(password));
        boolean save = super.save(sysUser);
        if (save) {
            //给用户新增角色
            if (!StringUtils.isEmpty(roleStrings)) {
                String[] roleIds = roleStrings.split(",");
                List<SysUserRole> sysUserRoleList = new ArrayList<>();
                for (String roleId : roleIds) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.parseLong(roleId));
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRoleList.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(sysUserRoleList);
            }
        }
        return save;
    }

    /**
     * <h2>修改员工</h2>
     * @param sysUser
     **/
    @Transactional
    @Override
    public boolean updateUser(SysUser sysUser) {
        String password = sysUser.getPassword();
        if (!Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}").matcher(password).matches()) {
            sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        }
        boolean update = super.updateById(sysUser);
        if (update) {
            //删除旧的角色
            sysUserRoleService.remove(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, sysUser.getId())
            );
            //新增新的角色
            String roleStrings = sysUser.getRole_strings();
            if (!StringUtils.isEmpty(roleStrings)) {
                String[] roleIds = roleStrings.split(",");
                List<SysUserRole> sysUserRoleList = new ArrayList<>();
                for (String roleId : roleIds) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(Long.parseLong(roleId));
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRoleList.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(sysUserRoleList);
            }
        }
        return update;
    }

    /**
     * <h2>删除用户的同时删除角色</h2>
     * @param idList
     **/
    @Transactional
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        boolean remove = super.removeByIds(idList);
        //删除角色
        sysUserRoleService.remove(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, idList)
        );
        return remove;
    }
}
