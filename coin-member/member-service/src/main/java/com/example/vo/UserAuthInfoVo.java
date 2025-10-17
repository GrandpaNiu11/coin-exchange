package com.example.vo;

import com.example.domin.User;
import com.example.domin.UserAuthAuditRecord;
import com.example.domin.UserAuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "用户认证的详细信息")
public class UserAuthInfoVo {

    @ApiModelProperty(value = "用户")
    private User user;

    @ApiModelProperty(value = "用户认证的详细列表")
    private List<UserAuthInfo> userAuthInfoList;

    @ApiModelProperty(value = "用户审核历史")
    private List<UserAuthAuditRecord> authAuditRecordList;
}
