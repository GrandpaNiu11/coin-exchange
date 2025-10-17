package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CashRechargeAuditRecordMapper;
import com.example.domin.CashRechargeAuditRecord;
import com.example.service.CashRechargeAuditRecordService;
@Service
public class CashRechargeAuditRecordServiceImpl extends ServiceImpl<CashRechargeAuditRecordMapper, CashRechargeAuditRecord> implements CashRechargeAuditRecordService{

}
