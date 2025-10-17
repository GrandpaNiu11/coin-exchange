package com.example.match;

import java.util.HashMap;
import java.util.Map;

public class MatchServiceFactory {

  private  static Map<MatchStrategy,MatchService> matchServiceMap =  new HashMap<>();

  //给我们的策略工厂里面添加一个交易实现类型
    public static void addMatchService(MatchStrategy symbol,MatchService matchService){
        matchServiceMap.put(symbol,matchService);
    }

    //使用策略名称获取具体的实现类
    public static MatchService getMatchService(MatchStrategy matchName){
        return matchServiceMap.get(matchName);
    }
}
