package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.UserDto;
import com.example.feign.UserServiceFeign;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domin.WorkIssue;
import com.example.mapper.WorkIssueMapper;
import com.example.service.WorkIssueService;
@Service
public class WorkIssueServiceImpl extends ServiceImpl<WorkIssueMapper, WorkIssue> implements WorkIssueService{

    @Autowired
    private UserServiceFeign userServiceFeign;
    /**
     * <h2>分页条件查询工单</h2>
     * @param page       分页参数
     * @param status     工单当前的处理状态
     * @param startTime  工单创建的起始时间
     * @param endTime    工单创建的截至时间
     **/
    @Override
    public Page<WorkIssue> findByPage(Page<WorkIssue> page, Integer status, String startTime, String endTime) {
        Page<WorkIssue> page1 = page(page, new LambdaQueryWrapper<WorkIssue>()
                .eq(status != null, WorkIssue::getStatus, status)
                .between(
                        !StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime),
                        WorkIssue::getCreated,
                        startTime,
                        endTime + " 23:59:59"
                )
        );

        List<WorkIssue> records = page1.getRecords();
        List<Long> collect = records.stream().map(item -> item.getUserId()).collect(  Collectors.toList());
        Map<Long, UserDto> basicUsers = userServiceFeign.getBasicUsers(collect, "", "");
        records.forEach(item -> {
                     if ( basicUsers.get(item.getUserId())!=null){
                         item.setUsername(basicUsers.get(item.getUserId()).getUsername());
                     }

                }
        );

        return page1;
    }

    @Override
    public Page<WorkIssue> getIssueList(Page<WorkIssue> page, Long userId) {
        return page(page,new LambdaQueryWrapper<WorkIssue>()
                .eq(WorkIssue::getUserId,userId));
        //.eq(WorkIssue::getStatus,1)
    }
}
