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
 * 角色
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role")
public class SysRole {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    /**
     * 名称
     */
    @TableField(value = "`name`")

    private String name;

    /**
     * 代码
     */
    @TableField(value = "code")

    private String code;

    /**
     * 描述
     */
    @TableField(value = "description")

    private String description;

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
     * 状态0:禁用 1:启用
     */
    @TableField(value = "`status`")

    private Byte status;

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