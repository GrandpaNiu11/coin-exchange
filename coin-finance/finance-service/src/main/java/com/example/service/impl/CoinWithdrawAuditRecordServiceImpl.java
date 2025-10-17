package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CoinWithdrawAuditRecordMapper;
import com.example.domin.CoinWithdrawAuditRecord;
import com.example.service.CoinWithdrawAuditRecordService;
@Service
public class CoinWithdrawAuditRecordServiceImpl extends ServiceImpl<CoinWithdrawAuditRecordMapper, CoinWithdrawAuditRecord> implements CoinWithdrawAuditRecordService{

}
