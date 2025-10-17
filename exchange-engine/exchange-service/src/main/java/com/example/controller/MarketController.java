package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domain.DepthItemVo;
import com.example.domin.Market;
import com.example.domin.TurnoverOrder;
import com.example.dto.MarketDto;
import com.example.feign.OrderBooksFeignClient;
import com.example.feign.marketServiceFeign;
import com.example.model.R;
import com.example.service.MarketService;
import com.example.service.TurnoverOrderService;
import com.example.vo.DepthsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/markets")
@Api(tags = "交易市场的控制器")
public class MarketController implements marketServiceFeign {
    @Autowired
    private MarketService marketService;

    @Autowired
    private TurnoverOrderService turnoverOrderService;



    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderBooksFeignClient orderBooksFeignClient;


    @GetMapping
    @ApiOperation(value = "交易市场的分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页"),
            @ApiImplicitParam(name = "size",value = "每页显示的条数"),
            @ApiImplicitParam(name = "tradeAreaId",value = "交易区域的id"),
            @ApiImplicitParam(name = "status",value = "交易区域的状态"),
    })
    @PreAuthorize("hasAuthority('trade_market_query')")
    public R<Page<Market>> findByPage(@ApiIgnore Page<Market> page, Long tradeAreaId, Byte status){
        Page<Market> pageData = marketService.findByPage(page,tradeAreaId,status);
        return R.ok(pageData);
    }


    @PostMapping("/setStatus")
    @ApiOperation(value = "启用、禁用交易市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market",value = "market的JSON数据")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R setStatus(@RequestBody Market market){
        boolean update = marketService.updateById(market);
        if (update){
            return R.ok();
        }
        return R.fail("状态设置失败");
    }

    @PostMapping
    @ApiOperation(value = "新增一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market",value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_create')")
    public R save(@RequestBody @Validated Market market){
        boolean save = marketService.save(market);
        if (save){
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @PatchMapping
    @ApiOperation(value = "修改一个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market",value = "marketjson")
    })
    @PreAuthorize("hasAuthority('trade_market_update')")
    public R update(@RequestBody @Validated Market market){
        boolean update = marketService.updateById(market);
        if (update){
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @GetMapping("/all")
    @ApiOperation(value = "查询所有的交易市场")
    public R<List<Market>> listMarkets(){
        return R.ok(marketService.list());
    }

    @Override
    public MarketDto findByCoinId(Long buyCoinId, Long sellCoinId) {
        MarketDto marketDto = marketService.findByCoinId(buyCoinId,sellCoinId);
        return marketDto;
    }

    @Override
    public MarketDto findBySymbol(String symbol) {
        Market markerBySymbol = marketService.getMarketBySymbol(symbol);
        MarketDto market = new MarketDto();
        BeanUtils.copyProperties(markerBySymbol,market);
        return market;
    }

    @ApiOperation(value = "通过的交易对以及深度查询当前的市场的深度数据")
    @GetMapping("/depth/{symbol}/{dept}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对"),
            @ApiImplicitParam(name = "dept", value = "深度类型"),
    })
    public R<DepthsVo> findDeptVosSymbol(@PathVariable("symbol") String symbol, @PathVariable("dept")String dept) {
        // 交易市场
        Market market = marketService.getMarketBySymbol(symbol);

        DepthsVo depthsVo = new DepthsVo();
        depthsVo.setCnyPrice(market.getOpenPrice()); // CNY的价格
        depthsVo.setPrice(market.getOpenPrice()); // GCN的价格
        Map<String, List<DepthItemVo>> stringListMap = orderBooksFeignClient.querySymbolDepth(symbol);
        if (!CollectionUtils.isEmpty(stringListMap)){
            depthsVo.setAsks(stringListMap.get("asks"));
            depthsVo.setBids(stringListMap.get("bids"));
        }

        return R.ok(depthsVo);

    }
    @ApiOperation(value = "查询成交记录")
    @GetMapping("/trades/{symbol}")
    public R<List<TurnoverOrder>> findSymbolTurnoverOrder(@PathVariable("symbol") String symbol) {
        List<TurnoverOrder> turnoverOrders = turnoverOrderService.findBySymbol(symbol);
        return R.ok(turnoverOrders);
    }


    



}
