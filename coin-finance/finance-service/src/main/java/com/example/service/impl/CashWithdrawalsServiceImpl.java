package com.example.service.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.CashWithdrawAuditRecord;
import com.example.dto.UserDto;
import com.example.feign.UserServiceFeign;
import com.example.mapper.CashWithdrawAuditRecordMapper;
import com.example.service.AccountService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CashWithdrawalsMapper;
import com.example.domin.CashWithdrawals;
import com.example.service.CashWithdrawalsService;
import org.springframework.util.CollectionUtils;

@Service
public class CashWithdrawalsServiceImpl extends ServiceImpl<CashWithdrawalsMapper, CashWithdrawals> implements CashWithdrawalsService{
    @Autowired
    private UserServiceFeign userServiceFeign;





    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CashWithdrawAuditRecordMapper cashWithdrawAuditRecordMapper;



    @CreateCache(name = "CASH_WITHDRAWALS_LOCK:",expire = 100,timeUnit = TimeUnit.SECONDS,cacheType = CacheType.BOTH)
    private Cache<String,String> lock;
    /*
     *  提现记录的查询
     * */
    @Override
    public Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {

        LambdaQueryWrapper<CashWithdrawals> cashWithdrawalsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 有用户的信息时
        Map<Long, UserDto> basicUsers = null;
        if (userId!=null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)){
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)){
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            cashWithdrawalsLambdaQueryWrapper.in(CashWithdrawals::getUserId,userIds);
        }
        // 其他的查询信息
        cashWithdrawalsLambdaQueryWrapper.eq(status!=null,CashWithdrawals::getStatus,status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashWithdrawals::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin)?"0":numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax)?"0":numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)),
                        CashWithdrawals::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<CashWithdrawals> pageData = page(page, cashWithdrawalsLambdaQueryWrapper);
        List<CashWithdrawals> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            List<Long> userIds = records.stream().map(CashWithdrawals::getUserId).collect(Collectors.toList());
            if (basicUsers == null){
                basicUsers = userServiceFeign.getBasicUsers(userIds,null,null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashWithdrawals -> {
                UserDto userDto = finalBasicUsers.get(cashWithdrawals.getUserId());
                if (userDto!=null){
                    cashWithdrawals.setUsername(userDto.getUsername());
                    cashWithdrawals.setRealName(userDto.getRealName());
                }
            });
        }
        return pageData;
    }

    @Override
    public boolean updateWithdrawalsStatus(Long userId, CashWithdrawAuditRecord cashWithdrawAuditRecord) {
        // 涉及金额类型的管理 必须加锁
        // 1.使用锁锁住
        boolean isOk = lock.tryLockAndRun(cashWithdrawAuditRecord.getId() + "",300, TimeUnit.SECONDS,()->{
            CashWithdrawals cashWithdrawals = getById(cashWithdrawAuditRecord.getId());
            if (cashWithdrawals == null){
                throw new IllegalArgumentException("现金的审核记录不存在");
            }
            // 2.添加一个审核的记录
            CashWithdrawAuditRecord cashWithdrawAuditRecordNew = new CashWithdrawAuditRecord();
            cashWithdrawAuditRecordNew.setAuditUserId(userId);
            cashWithdrawAuditRecordNew.setRemark(cashWithdrawAuditRecord.getRemark());
            cashWithdrawAuditRecordNew.setCreated(new Date());
            cashWithdrawAuditRecordNew.setStatus(cashWithdrawAuditRecord.getStatus());
            Integer step = cashWithdrawals.getStep() + 1;
            cashWithdrawAuditRecordNew.setStep(step.byteValue());
            cashWithdrawAuditRecordNew.setOrderId(cashWithdrawals.getId());


            cashWithdrawals.setStatus(cashWithdrawAuditRecord.getStatus());
            cashWithdrawals.setRemark(cashWithdrawAuditRecord.getRemark());
            cashWithdrawals.setLastTime(new Date());
            cashWithdrawals.setAccountId(userId); //审核的用户的id
            cashWithdrawals.setStep(step.byteValue());

            // 插入记录
            int count = cashWithdrawAuditRecordMapper.insert(cashWithdrawAuditRecordNew);
            if (cashWithdrawAuditRecord.getStatus() == 2){
                boolean update = updateById(cashWithdrawals);
                if (!update) {
                    throw new IllegalArgumentException("更新提现记录失败");
                }
            }else{// 审核通过
                Boolean isPass = accountService.decreaseAccountAmount(userId,
                        cashWithdrawals.getUserId(),cashWithdrawals.getCoinId(),cashWithdrawals.getNum(),
                        cashWithdrawals.getFee(),cashWithdrawals.getId(),cashWithdrawals.getRemark(),"withdrawals_out",(byte)2);
                if (isPass){
                    cashWithdrawals.setLastTime(new Date());
                    boolean update = updateById(cashWithdrawals);
                    if (!update){
                        throw new IllegalArgumentException("更新提现记录失败");
                    }
                }
            }
        });
        return isOk;
    }

    @Override
    public Page<CashWithdrawals> findUserCashWithdrawals(Page<CashWithdrawals> page, Long userId, Byte status) {
        return page(page,new LambdaQueryWrapper<CashWithdrawals>()
                .eq(CashWithdrawals::getUserId,userId)
                .eq(status != null,CashWithdrawals::getStatus,status));
    }
}
