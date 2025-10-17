package com.example.service;

import com.example.domin.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.SymbolAssetVo;
import com.example.vo.UserTotalAccountVo;

import java.math.BigDecimal;

public interface AccountService extends IService<Account>{


    /*
     *  用户资金的划转
     * */
    Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum,String remark,String businessType,Byte direction);


    Boolean decreaseAccountAmount(Long userId, Long userId1, Long coinId, BigDecimal num, BigDecimal fee, Long id, String remark, String withdrawalsOut, byte b);

    Account findByUserAndCoin(Long userId, String coinName);

    UserTotalAccountVo getUserTotalAccount(Long userId);

    SymbolAssetVo getSymbolAssert(String symbol, Long userId);

    void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee);
}
