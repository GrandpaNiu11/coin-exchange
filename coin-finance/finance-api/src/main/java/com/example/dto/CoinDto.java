package com.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "coin的RPC传输对象")
public class CoinDto {


    /**
     * 币种ID
     */
    @ApiModelProperty(value="币种ID")
    private Long id;

    /**
     * 币种名称
     */
    @ApiModelProperty(value="币种名称")
    private String name;

    /**
     * 币种标题
     */
    @ApiModelProperty(value="币种标题")
    private String title;
    /**
     * 币种logo
     */
    @ApiModelProperty(value="币种logo")
    private String img;

    /**
     * 最小提现单位
     */
    @ApiModelProperty(value="最小提现单位")
    private BigDecimal baseAmount;

    /**
     * 单笔最小提现数量
     */
    @ApiModelProperty(value="单笔最小提现数量")
    private BigDecimal minAmount;

    /**
     * 单笔最大提现数量
     */
    @ApiModelProperty(value="单笔最大提现数量")
    private BigDecimal maxAmount;

    /**
     * 当日最大提现数量
     */
    @ApiModelProperty(value="当日最大提现数量")
    private BigDecimal dayMaxAmount;
}
