package example.service.impl;


import example.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service(value = "UserServiceDetailsImpl")
public class UserServiceDetailsImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 登录的实现
     * @param username
     **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginType = requestAttributes.getRequest().getParameter("login_type");
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("请添加login_type参数");
        }
        UserDetails userDetails = null;
        try {
            //使用refresh_token刷新token
            String grantType = requestAttributes.getRequest().getParameter("grant_type");
            if (LoginConstant.REFRESH_TYPE.equals(grantType.toUpperCase())) {
                username = adjustUsername(username, loginType);
            }
            switch (loginType) {
                case LoginConstant.ADMIN_TYPE: //管理员登录
                    userDetails = loadSysUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE:
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("暂不支持的登录方式" + loginType);
            }
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new UsernameNotFoundException("用户名：" + username + " 不存在");
        }

        return userDetails;
    }

    /**
     * 纠正在refresh 场景下的登录问题,用户的名称
     * @param username 用户的id
     * @param loginType admin_type member_type
     **/
    private String adjustUsername(String username, String loginType) {
        if (LoginConstant.ADMIN_TYPE.equals(loginType)) {
            //后台会员/管理员
            return jdbcTemplate.queryForObject(
                    LoginConstant.QUERY_ADMIN_USER_WITH_ID,
                    String.class,
                    username);
        }
        if (LoginConstant.MEMBER_TYPE.equals(loginType)) {
            //前端用户
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_USER_WITH_ID, String.class, username);
        }
        return username;
    }

    /**
     *  前台会员登录
     * @param username 会员名字
     **/
    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_SQL, (rs, i) -> {
            if (rs.wasNull()) {
                throw new UsernameNotFoundException("用户：" + username + " 不存在");
            }
            long id = rs.getLong("id");
            String password = rs.getString("password");
            int status = rs.getInt("status");
            // 3、封装成一个UserDetails对象返回
            return new User(
                    String.valueOf(id),
                    password,
                    status == 1,
                    true,
                    true,
                    true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }, username, username);
    }

    /**
     * 后台管理人员登录
     * @param username 用户名
     **/
    private UserDetails loadSysUserByUsername(String username) {
        // 1、根据用户名查询用户
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, (rs, i) -> {
            if (rs.wasNull()) {
                throw new UsernameNotFoundException("用户名：" + username + " 不存在");
            }
            long id = rs.getLong("id");
            String password = rs.getString("password");
            int status = rs.getInt("status");
            // 3、封装成一个UserDetails对象返回
            return new User(
                    String.valueOf(id),
                    password,
                    status == 1,
                    true,
                    true,
                    true,
                    getUserPermissions(id) // 2、查询该用户对应的权限
            );
        }, username);

    }

    /**
     *     后台管理人员登录
     *     通过用户的id 获取用户的权限
     * @param id 用户的id
     **/
    private Collection<? extends GrantedAuthority> getUserPermissions(long id) {
        // 1、当用户为超级管理员时，拥有所有的权限数据
        String code = jdbcTemplate.queryForObject(LoginConstant.QUERY_ROLE_CODE_SQL, String.class, id);
        List<String> permissions = null;
        if (LoginConstant.ADMIN_ROLE_CODE.equals(code)) {
            // 2、超级用户, 全部的权限
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS, String.class);
        } else {
            // 2、普通用户，需要使用角色->权限数据
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
        }
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .distinct() //去重
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
