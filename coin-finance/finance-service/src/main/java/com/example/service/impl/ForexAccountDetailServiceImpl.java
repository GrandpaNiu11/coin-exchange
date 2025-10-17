package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ForexAccountDetailMapper;
import com.example.domin.ForexAccountDetail;
import com.example.service.ForexAccountDetailService;
@Service
public class ForexAccountDetailServiceImpl extends ServiceImpl<ForexAccountDetailMapper, ForexAccountDetail> implements ForexAccountDetailService{

}
