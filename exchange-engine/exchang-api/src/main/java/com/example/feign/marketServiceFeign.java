package com.example.feign;

import com.example.dto.MarketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "exchange-service",contextId = "marketServiceFeign",configuration = OAuth2FeignConfig.class,path = "markets")
public interface marketServiceFeign {


    /**
     *  使用报价货币(你自己的) 以及出售货币(你要买的)的ID 查询
     * */
    @GetMapping("/getMarket")
    MarketDto findByCoinId(@RequestParam("buyCoinId") Long buyCoinId, @RequestParam("sellCoinId")Long sellCoinId);

    @GetMapping("/getMarket/symbol")
    MarketDto findBySymbol(@RequestParam("symbol") String symbol);

}
