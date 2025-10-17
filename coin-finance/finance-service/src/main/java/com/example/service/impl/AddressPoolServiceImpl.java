package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.AddressPoolMapper;
import com.example.domin.AddressPool;
import com.example.service.AddressPoolService;
@Service
public class AddressPoolServiceImpl extends ServiceImpl<AddressPoolMapper, AddressPool> implements AddressPoolService{

}
