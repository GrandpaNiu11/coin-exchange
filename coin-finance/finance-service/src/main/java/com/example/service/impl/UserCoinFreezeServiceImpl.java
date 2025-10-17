package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserCoinFreezeMapper;
import com.example.domin.UserCoinFreeze;
import com.example.service.UserCoinFreezeService;
@Service
public class UserCoinFreezeServiceImpl extends ServiceImpl<UserCoinFreezeMapper, UserCoinFreeze> implements UserCoinFreezeService{

}
