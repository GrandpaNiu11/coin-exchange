package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.CashRecharge;
import com.example.domin.CashRechargeAuditRecord;
import com.example.model.CashParam;
import com.example.model.R;
import com.example.service.CashRechargeService;
import com.example.vo.CashTradeVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/cashRecharges")
@ApiOperation(value = "GCN控制器")
public class CashRechargeController {

    @Autowired
    private CashRechargeService cashRechargeService;

    @GetMapping("/records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数"),
            @ApiImplicitParam(name = "coinId",value = "币种Id"),
            @ApiImplicitParam(name = "userId",value = "用户的ID"),
            @ApiImplicitParam(name = "userName",value = "用户的名称"),
            @ApiImplicitParam(name = "mobile",value = "用户的手机号"),
            @ApiImplicitParam(name = "status",value = "充值的状态"),
            @ApiImplicitParam(name = "numMin",value = "充值金额的最小值"),
            @ApiImplicitParam(name = "numMax",value = "充值金额的最大值"),
            @ApiImplicitParam(name = "startTime",value = "充值开始时间"),
            @ApiImplicitParam(name = "endTime",value = "充值结束时间")
    })
    public R<Page<CashRecharge>> findByPage(
            @ApiIgnore Page<CashRecharge> page, Long coinId,
            Long userId, String userName, String mobile,
            Byte status, String numMin, String numMax,
            String startTime, String endTime
    ){
        Page<CashRecharge> pageData = cashRechargeService.findByPage(page,coinId,userId,userName
                ,mobile,status,numMin,numMax,startTime,endTime);
        return R.ok(pageData);
    }

    @PostMapping("/cashRechargeUpdateStatus")
    @ApiOperation(value = "现金的充值审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cashRecharge", value = "cashRecharge json数据")
    })
    public R cashRechargeAudit(@RequestBody CashRechargeAuditRecord auditRecord) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean isOk = cashRechargeService.cashRechargeAudit(Long.valueOf(userId), auditRecord);
        return isOk ? R.ok():R.fail("审核失败");
    }
    @GetMapping("/user/records")
    @ApiOperation(value = "查询当前用户充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="status" ,value = "充值的状态"),
            @ApiImplicitParam(name ="current" ,value = "当前页"),
            @ApiImplicitParam(name ="size" ,value = "每页条目"),
    })
    public R<Page<CashRecharge>> findUserCashRecharge(@ApiIgnore Page<CashRecharge> page, Byte status) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Page<CashRecharge> cashRechargePage = cashRechargeService.findUserCashRecharge(page,Long.valueOf(userId),status);
        return R.ok(cashRechargePage);
    }

    @PostMapping("/buy")
    @ApiOperation(value = "GCN买入")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="cashParam" ,value = "现金交易的参数"),
    })
    public R<CashTradeVo> buy(@RequestBody @Validated CashParam cashParam) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        CashTradeVo cashTradeVo = cashRechargeService.buy(Long.valueOf(userId), cashParam);
        return R.ok(cashTradeVo);
    }

}
