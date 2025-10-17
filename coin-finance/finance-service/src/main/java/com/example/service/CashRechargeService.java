package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.CashRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domin.CashRechargeAuditRecord;
import com.example.model.CashParam;
import com.example.vo.CashTradeVo;

public interface CashRechargeService extends IService<CashRecharge>{


    Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);


    boolean cashRechargeAudit(Long aLong, CashRechargeAuditRecord auditRecord);

    Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long aLong, Byte status);

    CashTradeVo buy(Long aLong, CashParam cashParam);

}
