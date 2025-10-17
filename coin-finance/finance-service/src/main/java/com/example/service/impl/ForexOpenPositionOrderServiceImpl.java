package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ForexOpenPositionOrderMapper;
import com.example.domin.ForexOpenPositionOrder;
import com.example.service.ForexOpenPositionOrderService;
@Service
public class ForexOpenPositionOrderServiceImpl extends ServiceImpl<ForexOpenPositionOrderMapper, ForexOpenPositionOrder> implements ForexOpenPositionOrderService{

}
