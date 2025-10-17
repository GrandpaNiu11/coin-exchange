package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.UserDto;
import com.example.model.RegisterParam;
import com.example.model.UpdateLoginParam;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User>{


    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Object o);


    Page<User> findDirectInvitePage(Page<User> page, Long userId);

    void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark);

    boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam);

    Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile);

    boolean register(RegisterParam registerParam);

}
