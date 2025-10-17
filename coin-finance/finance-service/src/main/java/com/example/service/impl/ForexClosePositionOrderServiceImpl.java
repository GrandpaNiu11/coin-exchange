package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.ForexClosePositionOrder;
import com.example.mapper.ForexClosePositionOrderMapper;
import com.example.service.ForexClosePositionOrderService;
@Service
public class ForexClosePositionOrderServiceImpl extends ServiceImpl<ForexClosePositionOrderMapper, ForexClosePositionOrder> implements ForexClosePositionOrderService{

}
