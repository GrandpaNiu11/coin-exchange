package com.example.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.UserAuthAuditRecord;
import com.example.dto.UserDto;
import com.example.geetest.GeetestLib;
import com.example.mappers.UserDtoMapperImpl;
import com.example.model.RegisterParam;
import com.example.model.UpdateLoginParam;
import com.example.service.SmsService;
import com.example.service.UserAuthAuditRecordService;
import com.example.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.domin.User;
import com.example.service.UserService;
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Autowired
    private UserAuthAuditRecordService userAuthAuditRecordService;
    @Autowired
    private UserAuthInfoService userAuthInfoService;


    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SmsService smsService;

    @Override
    public Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status,Object reviewsStatus) {
        return page(page,
                new LambdaQueryWrapper<User>()
                        .like(!StringUtils.isEmpty(mobile),User::getMobile,mobile)
                        .like(!StringUtils.isEmpty(userName),User::getUsername,userName)
                        .like(!StringUtils.isEmpty(realName),User::getRealName,realName)
                        .eq(userId!=null,User::getId,userId)
                        .eq(status!=null,User::getStatus,status)
                        .eq(reviewsStatus!=null,User::getReviewsStatus,reviewsStatus)

        );
    }

    @Override
    public Page<User> findDirectInvitePage(Page<User> page, Long userId) {
        return page(page,new LambdaQueryWrapper<User>().eq(User::getDirectInviteid,userId));
    }

    @Override
    public void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark) {
        User user = getById(id);
        if (user!= null){
            //user.setAuthStatus(authStatus); // 认证的状态
            user.setReviewsStatus(authStatus.intValue()); //审核的状态
            updateById(user); // 修改用户的状态
        }
        UserAuthAuditRecord userAuthAuditRecord = new UserAuthAuditRecord();
        userAuthAuditRecord.setUserId(id);
        userAuthAuditRecord.setStatus(authStatus);
        userAuthAuditRecord.setAuthCode(authCode);

        String usrStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        userAuthAuditRecord.setAuditUserId(Long.valueOf(usrStr));   // 审核人的ID
        userAuthAuditRecord.setAuditUserName("--------------");   // 审核人的名称 -> 远程调用admin-service，没有书屋
        userAuthAuditRecord.setRemark(remark);   // 审核人的名称

        userAuthAuditRecordService.save(userAuthAuditRecord);

    }

    /*
     *  修改用户的登录密码
     * */
    @Override
    public boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam) {

        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的Id错误");
        }
        String oldpassword = updateLoginParam.getOldpassword();
        // 1.校验之前的密码 数据库的密码都是我们加密后的密码
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


        // 密码匹配
        boolean matches = bCryptPasswordEncoder.matches(oldpassword, user.getPassword());
        if (!matches){
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        user.setPassword(bCryptPasswordEncoder.encode(updateLoginParam.getNewpassword()));
        return updateById(user);
    }

    @Override
    public Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile) {
        // 使用ids进行用户的批量查询，用在我们给别人远程调用时批量获取用户的数据
        // 使用用户名、手机号查询一系列用户的集合
        if (CollectionUtils.isEmpty(ids)&&StringUtils.isEmpty(userName)&&StringUtils.isEmpty(mobile)){
            return Collections.emptyMap();
        }
        List<User> list = list(new LambdaQueryWrapper<User>()
                .in(!CollectionUtils.isEmpty(ids),User::getId, ids)
                .like(!StringUtils.isEmpty(userName),User::getUsername,userName)
                .like(!StringUtils.isEmpty(mobile),User::getMobile,mobile))
                ;
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyMap();
        }
        // 经过批量in查询后，进行 entity->Dto的映射 User->UserDto
        List<UserDto> userDtos = UserDtoMapperImpl.convert2Dto(list);
        Map<Long, UserDto> userDtoIdMappings = userDtos.stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        return userDtoIdMappings;



    }

    @Override
    public boolean register(RegisterParam registerParam) {
        log.info("用户开始注册{}", JSON.toJSONString(registerParam,true));
        String mobile = registerParam.getMobile();
        String email = registerParam.getEmail();
        // 这里判断 mobile和email是否重复
        // 1.简单校验
        if (StringUtils.isEmpty(email)&&StringUtils.isEmpty(mobile)){
            throw new IllegalArgumentException("手机号或邮箱不能同时为空");
        }
        // 2.查询校验
        int count = count(new LambdaQueryWrapper<User>()
                .eq(!StringUtils.isEmpty(email), User::getEmail, email)
                .eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (count > 0){
            throw new IllegalArgumentException("手机号或邮箱已经被注册");
        }

        // 进行极验的校验
//        registerParam.check(geetestLib,redisTemplate);
        // 构建一个新的用户
        User user = getUser(registerParam);

        return save(user);
    }

    private User getUser(RegisterParam registerParam) {
        User user = new User();

        user.setEmail(registerParam.getEmail());
        user.setMobile(registerParam.getMobile());

        String encodePwd = new BCryptPasswordEncoder().encode(registerParam.getPassword());
        user.setPassword(encodePwd);
        user.setPaypassSetting(false);
        user.setStatus((byte) 1);
        user.setType((byte) 1);
        user.setAuthStatus((byte) 0);
        user.setLogins(0);
        // 用户的邀请码
        user.setInviteCode(RandomUtil.randomString(6));

        if (!StringUtils.isEmpty(registerParam.getInvitionCode())){
            User userPre = getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, registerParam.getInvitionCode()));
            if (userPre != null){
                // 邀请人的id，需要查询
                user.setDirectInviteid(String.valueOf(userPre.getId()));
                // 邀请关系
                user.setInviteRelation(String.valueOf(userPre.getId()));
            }
        }

        return user;

    }


    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode1 = bCryptPasswordEncoder.encode("shenzhuoran521");
        String encode = bCryptPasswordEncoder.encode("shenzhuoran521");
        boolean matches = bCryptPasswordEncoder.matches(encode, encode1);

        System.out.println( matches);
    }
}
