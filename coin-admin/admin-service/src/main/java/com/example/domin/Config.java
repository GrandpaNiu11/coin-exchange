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
 * 平台配置信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "config")
public class Config {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)

    private Long id;

    /**
     * 配置规则类型
     */
    @TableField(value = "`type`")

    private String type;

    /**
     * 配置规则代码
     */
    @TableField(value = "code")

    private String code;

    /**
     * 配置规则名称
     */
    @TableField(value = "`name`")

    private String name;

    /**
     * 配置规则描述
     */
    @TableField(value = "`desc`")

    private String desc;

    /**
     * 配置值
     */
    @TableField(value = "`value`")

    private String value;

    /**
     * 创建时间
     */
    @TableField(value = "created")

    private Date created;
}