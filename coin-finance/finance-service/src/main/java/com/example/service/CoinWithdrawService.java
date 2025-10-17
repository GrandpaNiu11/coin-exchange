package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.CoinWithdraw;
import com.baomidou.mybatisplus.extension.service.IService;
public interface CoinWithdrawService extends IService<CoinWithdraw>{


    Page<CoinWithdraw> findByPage(Page<CoinWithdraw> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);

    Page<CoinWithdraw> findUserCoinRecharge(Page<CoinWithdraw> page, Long coinId, Long userId);
}
