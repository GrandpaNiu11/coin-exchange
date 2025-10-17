package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.constant.Constants;
import com.example.domin.AccountDetail;
import com.example.domin.Coin;
import com.example.domin.Config;
import com.example.dto.MarketDto;
import com.example.feign.marketServiceFeign;
import com.example.service.AccountDetailService;
import com.example.service.CoinService;
import com.example.service.ConfigService;

import com.example.vo.AccountVo;
import com.example.vo.SymbolAssetVo;
import com.example.vo.UserTotalAccountVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{
    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private marketServiceFeign marketServiceFeignn;
    /*
     *  获取资金账户
     * */
    private Account getCoinAccount(Long userId, Long coinId) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getUserId, userId)
                .eq(Account::getCoinId, coinId)
                .eq(Account::getStatus, 1)
        );
    }

    /*
     *  用户资金的划转
     * */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum,String remark,String businessType,Byte direction) {
        Account coinAccount = getCoinAccount(userId, coinId);
        if (coinAccount == null){
            throw new IllegalArgumentException("用户当前的该币种的余额不存在");
        }
        // 增加一条流水记录
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCoinId(coinId);
        accountDetail.setUserId(userId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setOrderId(orderNum);
        accountDetail.setAccountId(adminId);
        accountDetail.setRefAccountId(adminId);
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(Integer.valueOf(direction));
        accountDetail.setCreated(new Date());
        boolean save = accountDetailService.save(accountDetail);
        if (save){
            // 用户余额的增加
            coinAccount.setBalanceAmount(coinAccount.getBalanceAmount().add(num));
            boolean update = updateById(coinAccount);
            return update;
        }
        return save;
    }

    @Override
    public Boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum, String remark, String businessType, byte direction) {
        // 查询充钱、扣钱账户是否存在
        Account coinAccount = getCoinAccount(userId, coinId);
        if (coinAccount == null){
            throw new IllegalArgumentException("账户不存在");
        }
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUserId(userId);
        accountDetail.setCoinId(coinId);
        accountDetail.setAmount(num);
        accountDetail.setFee(fee);
        accountDetail.setAccountId(coinAccount.getId());
        accountDetail.setRefAccountId(coinAccount.getId());
        accountDetail.setRemark(remark);
        accountDetail.setBusinessType(businessType);
        accountDetail.setDirection(Integer.valueOf(direction));
        // 插入账户流水
        boolean save = accountDetailService.save(accountDetail);
        if (save){
            // 新增流水
            BigDecimal balanceAmount = coinAccount.getBalanceAmount();
            BigDecimal result = balanceAmount.add(num.multiply(BigDecimal.valueOf(-1)));
            if (result.compareTo(BigDecimal.ONE) > 0){
                // 账户流水扣钱
                coinAccount.setBalanceAmount(result);
                return updateById(coinAccount);
            }else{
                throw new IllegalArgumentException("余额不足");
            }
        }

        return false;

    }

    @Override
    public Account findByUserAndCoin(Long userId, String coinName) {
        // 先根据货币名称查询出货币id
        Coin coin = coinService.getCoinByCoinName(coinName);
        if (coin == null){
            throw new IllegalArgumentException("货币不存在");
        }
        Account account = getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getUserId, userId)
                .eq(Account::getCoinId, coin.getId()));

        if (account == null){
            throw new IllegalArgumentException("该资产不存在");
        }
        // 在config表中查询出 cny的买入卖出比率
        Config sellRateConfig = configService.getConfigByCode(Constants.SELL_GCN_RATE);
        account.setSellRate(new BigDecimal(sellRateConfig.getValue())); //出售的比率

        Config setBuyRateConfig = configService.getConfigByCode(Constants.BUY_GCN_RATE);
        account.setBuyRate(new BigDecimal(setBuyRateConfig.getValue())); //买进来的比率
        return account;
    }

    @Override
    public UserTotalAccountVo getUserTotalAccount(Long userId) {
        // 计算总资产
        UserTotalAccountVo userTotalAccountVo = new UserTotalAccountVo();
        BigDecimal basicCoin2Cny = BigDecimal.ONE; // 平台币对人民币的汇率
        BigDecimal basicCoin = BigDecimal.ZERO; // 平台币
        List<AccountVo> assertList = new ArrayList<AccountVo>();
        // 用户的总资产位于Account里面
        List<Account> accounts = list(new LambdaQueryWrapper<Account>()
                .eq(Account::getUserId, userId));
        if (CollectionUtils.isEmpty(accounts)){
            userTotalAccountVo.setAssertList(assertList);
            userTotalAccountVo.setAmountUs(BigDecimal.ZERO);
            userTotalAccountVo.setAmount(BigDecimal.ZERO);
            return userTotalAccountVo;
        }
        for (Account account : accounts) {
            AccountVo accountVo = new AccountVo();
            BeanUtils.copyProperties(account, accountVo);
            Long coinId = account.getCoinId();
            Coin coin = coinService.getById(coinId);
            if (coin == null || coin.getStatus()!= (byte)1){
                continue;
            }

            // 设置币的信息
            accountVo.setCoinName(coin.getName());
            accountVo.setCoinImgUrl(coin.getImg());
            accountVo.setCoinType(coin.getType());
            accountVo.setWithdrawFlag(coin.getWithdrawFlag());
            accountVo.setRechargeFlag(coin.getRechargeFlag());
            accountVo.setFeeRate(BigDecimal.valueOf(coin.getRate()));
            accountVo.setMinFeeNum(coin.getMinFeeNum());

            assertList.add(accountVo);
            // 计算总的账面余额
            BigDecimal volume = accountVo.getBalanceAmount().add(accountVo.getFreezeAmount());
            accountVo.setCarryingAmount(volume); //总的账面余额
            // 将该币和我们系统统计币使用的基币进行转化
            BigDecimal currentPrice = getCurrentCoinPrice(coinId);
            BigDecimal total = volume.multiply(currentPrice);

            basicCoin = basicCoin.add(total); // 将该子资产添加到我们的总资产里面
        }
        userTotalAccountVo.setAmount(basicCoin.multiply(basicCoin2Cny).setScale(8, RoundingMode.HALF_UP)); //总的人民币
        userTotalAccountVo.setAmountUs(basicCoin); // 总的平台计算的币种(基础币)
        userTotalAccountVo.setAssertList(assertList);

        return userTotalAccountVo;

    }

    @Override
    public SymbolAssetVo getSymbolAssert(String symbol, Long userId) {
        /**
         * 远程调用获取市场
         */
        MarketDto marketDto = marketServiceFeignn.findBySymbol(symbol);
        SymbolAssetVo symbolAssetVo = new SymbolAssetVo();
        // 查询报价货币
        Long buyCoinId = marketDto.getBuyCoinId(); // 报价货币的Id
        Account buyCoinAccount = getCoinAccount(userId, buyCoinId);
        symbolAssetVo.setBuyAmount(buyCoinAccount.getBalanceAmount());
        symbolAssetVo.setBuyLockAmount(buyCoinAccount.getFreezeAmount());
        // 市场里面配置的值
        symbolAssetVo.setBuyFeeRate(marketDto.getFeeBuy());
        Coin buyCoin = coinService.getById(buyCoinId);
        symbolAssetVo.setBuyUnit(buyCoin.getName());
        // 查询基础汇报
        @NotBlank Long sellCoinId = marketDto.getSellCoinId();
        Account coinAccount = getCoinAccount(userId, sellCoinId);
        symbolAssetVo.setSellAmount(coinAccount.getBalanceAmount());
        symbolAssetVo.setSellLockAmount(coinAccount.getFreezeAmount());
        // 市场里面配置的值
        symbolAssetVo.setSellFeeRate(marketDto.getFeeSell());
        Coin sellCoin = coinService.getById(sellCoinId);
        symbolAssetVo.setSellUnit(sellCoin.getName());

        return symbolAssetVo;
    }

    @Override
    public void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee) {
        Account account = getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId)
                .eq(Account::getCoinId, coinId));
        if (account == null){
            throw new IllegalArgumentException("您输入的资产类型不存在");
        }
        BigDecimal balanceAmount = account.getBalanceAmount();
        // 判断余额够不够 是否满足扣减状态
        if (balanceAmount.compareTo(mum) < 0){
            throw new IllegalArgumentException("账户的资金不足");
        }
        // 库存的操作
        account.setBalanceAmount(balanceAmount.subtract(mum));
        account.setFreezeAmount(account.getFreezeAmount().add(mum));
        boolean update = updateById(account);
        if (update){
            // 如果扣减成功 增加流水记录
            AccountDetail accountDetail = new AccountDetail(
                    null,
                    userId,
                    coinId,
                    account.getId(),
                    account.getId(), // 如果该订单是邀请奖励，有refAccountId,否则，根accountId一样
                    orderId,
                2,
                    type,
                    mum,
                    fee,
                    "用户提现",
                    null,
                    null,
                    null
            );
        }
    }

    private BigDecimal getCurrentCoinPrice(Long coinId) {
        // 1.查询我们的基础币是什么
        Config configBasicCoin = configService.getConfigByCode("PLATFORM_COIN_ID"); //基础币
        if (configBasicCoin == null){
            throw new IllegalArgumentException("请配置基础币后使用");
        }
        Long basicCoinId = Long.valueOf(configBasicCoin.getValue());
        if (coinId.equals(basicCoinId)){
            // 该币就是基础币
            return BigDecimal.ONE;
        }
        // 不等于，我们需要查询交易市场，使用基础币作为我们报价货币，使用报价货币的金额 来计算当前币的价格
        MarketDto market = marketServiceFeignn.findByCoinId(basicCoinId, coinId);
        if (market!=null){
            //  存在这个交易对
            return market.getOpenPrice();
        }else {
            // 该交易对不存在
            log.error("不存在当前币和平台币兑换的市场,请后台人员即时添加");
            return BigDecimal.ZERO;
        }
    }


}
