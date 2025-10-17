package com.example.domin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限配置
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_privilege")
public class SysPrivilege {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    /**
     * 所属菜单Id
     */
    @TableField(value = "menu_id")

    private Long menuId;

    /**
     * 功能点名称
     */
    @TableField(value = "`name`")

    private String name;

    /**
     * 功能描述
     */
    @TableField(value = "description")

    private String description;

    @TableField(value = "url")

    private String url;

    @TableField(value = "`method`")

    private String method;

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

    //在使用角色查询权限时,我们也会使用该字段来标记当前的角色使用有该权限,有权限为1,否则为0
    @TableField(exist = false)
    @ApiModelProperty(value="当前角色是否拥有这个权限")
    private int own ;
}