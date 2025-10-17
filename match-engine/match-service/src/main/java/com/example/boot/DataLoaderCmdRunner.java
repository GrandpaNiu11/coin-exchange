package com.example.boot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.disruptor.DisruptorTemplate;
import com.example.domin.EntrustOrder;
import com.example.enums.OrderDirection;
import com.example.mapper.EntrustOrderMapper;
import com.example.model.Order;
import com.example.utils.BeanUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DataLoaderCmdRunner  implements CommandLineRunner {

    @Autowired
    private EntrustOrderMapper entrustOrderMapper;

    @Autowired
    private DisruptorTemplate disruptorTemplate;

//  项目 启动时加载数据
    @Override
    public void run(String... args) throws Exception {
        List<EntrustOrder> entrustOrders = entrustOrderMapper.selectList(
                new LambdaQueryWrapper<EntrustOrder>().eq(EntrustOrder::getStatus, 0).orderByAsc(EntrustOrder::getCreated)
        );
        if (CollectionUtils.isEmpty(entrustOrders)){
            return;
        }
        for (EntrustOrder entrustOrder : entrustOrders){
            disruptorTemplate.onData(BeanUtils.entrustOrder20rder(entrustOrder));
        }
    }


}
