package com.example.service;

import com.example.domin.UserAuthAuditRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthAuditRecordService extends IService<UserAuthAuditRecord>{


    List<UserAuthAuditRecord> getUserAuthAuditRecordList(Long id);

}
