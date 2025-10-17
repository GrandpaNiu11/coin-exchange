package com.example.model;

import com.example.domin.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录结果")
public class LoginResult {

    @ApiModelProperty(value = "token")
    private String token;


    @ApiModelProperty(value = "菜单")
    private List<SysMenu> menus;

    @ApiModelProperty(value = "权限")
    private List<SimpleGrantedAuthority> authorities;

}
