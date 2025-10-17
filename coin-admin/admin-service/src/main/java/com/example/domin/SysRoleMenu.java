package com.example.domin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色菜单
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role_menu")
public class SysRoleMenu {
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    @TableField(value = "role_id")

    private Long roleId;

    @TableField(value = "menu_id")

    private Long menuId;

    /**
     * 创建人
     */
    @TableField(value = "create_by")

    private Long createBy;

    /**
     * 修改人
     */
    @TableField(value = "modify_by")

    private Long modifyBy;

    /**
     * 创建时间
     */
    @TableField(value = "created")

    private Date created;

    /**
     * 修改时间
     */
    @TableField(value = "last_update_time")

    private Date lastUpdateTime;
}