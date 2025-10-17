package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.UserAccountFreeze;
import com.example.mapper.UserAccountFreezeMapper;
import com.example.service.UserAccountFreezeService;
@Service
public class UserAccountFreezeServiceImpl extends ServiceImpl<UserAccountFreezeMapper, UserAccountFreeze> implements UserAccountFreezeService{

}
