package com.example.domin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统菜单
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_menu")
public class SysMenu {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    /**
     * 上级菜单ID
     */
    @TableField(value = "parent_id")

    private Long parentId;

    /**
     * 上级菜单唯一KEY值
     */
    @TableField(value = "parent_key")

    private String parentKey;

    /**
     * 类型 1-分类 2-节点
     */
    @TableField(value = "`type`")

    private Byte type;

    /**
     * 名称
     */
    @TableField(value = "`name`")

    private String name;

    /**
     * 描述
     */
    @TableField(value = "`desc`")

    private String desc;

    /**
     * 目标地址
     */
    @TableField(value = "target_url")

    private String targetUrl;

    /**
     * 排序索引
     */
    @TableField(value = "sort")

    private Integer sort;

    /**
     * 状态 0-无效； 1-有效；
     */
    @TableField(value = "`status`")

    private Byte status;

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

    @TableField(exist = false)
    @ApiModelProperty("该菜单下的所有的权限")
    private List<SysPrivilege> privileges;

    @TableField(exist = false)
    @ApiModelProperty("该菜单的子菜单")
    private List<SysMenu>  childs ;


    @TableField(exist = false)
    @ApiModelProperty("该菜单的唯一Key值")
    private  String menuKey ;

    /**
     * 获取菜单的唯一Key凭证
     * @return
     */
    public String getMenuKey() {
        if (!StringUtils.isEmpty(parentKey)) {
            return parentKey+"."+id;
        }else {
            return id.toString();
        }
    }

}