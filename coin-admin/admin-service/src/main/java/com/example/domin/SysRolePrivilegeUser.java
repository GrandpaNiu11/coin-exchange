package com.example.domin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户权限配置
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role_privilege_user")
public class SysRolePrivilegeUser {
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    /**
     * 角色Id
     */
    @TableField(value = "role_id")

    private Long roleId;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")

    private Long userId;

    /**
     * 权限Id
     */
    @TableField(value = "privilege_id")

    private Long privilegeId;
}