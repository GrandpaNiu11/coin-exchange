package com.example.domin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人民币充值卡号管理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "admin_bank")
public class AdminBank {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 开户人姓名
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 开户行名称
     */
    @TableField(value = "bank_name")
    private String bankName;

    /**
     * 卡号
     */
    @TableField(value = "bank_card")
    private String bankCard;

    /**
     * 充值转换换币种ID
     */
    @TableField(value = "coin_id")
    private Long coinId;

    /**
     * 币种名称
     */
    @TableField(value = "coin_name")
    private String coinName;

    /**
     * 状态：0-无效；1-有效；
     */
    @TableField(value = "`status`")
    private Byte status;
}