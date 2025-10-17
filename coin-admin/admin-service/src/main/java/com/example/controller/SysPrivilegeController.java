package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domin.SysPrivilege;
import com.example.model.R;
import com.example.service.SysPrivilegeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PreDestroy;
import java.util.List;

@RestController
@RequestMapping("/privileges")
public class SysPrivilegeController {

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys_privilege_query')")
    public R<Page<SysPrivilege>> findByPage(@ApiIgnore  Page<SysPrivilege> page) {

         page.addOrder(OrderItem.desc("last_update_time"));
         Page<SysPrivilege> pageData = sysPrivilegeService.page(page);

        return R.ok(pageData);
    }

    @PostMapping
    @ApiOperation(value = "新增一个权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysPrivilege" ,value = "sysPrivilege 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_privilege_create')")
    public R add(@RequestBody @Validated SysPrivilege sysPrivilege){
        boolean save = sysPrivilegeService.save(sysPrivilege);
        if(save){
            return R.ok("新增成功") ;
        }
        return  R.fail("新增失败") ;
    }

    @PatchMapping
    @ApiOperation(value = "修改一个权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysPrivilege" ,value = "sysPrivilege 的json数据")
    })
    @PreAuthorize("hasAuthority('sys_privilege_update')")
    public R update(@RequestBody @Validated SysPrivilege sysPrivilege){
        boolean save = sysPrivilegeService.updateById(sysPrivilege);
        if(save){
            return R.ok("修改成功") ;
        }
        return  R.fail("修改失败") ;
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一个权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysPrivilege" ,value = "权限id的str集合")
    })
    @PreAuthorize("hasAuthority('sys_privilege_delete')")
    public R delete(List<String> ids) {
        ids.forEach(id -> sysPrivilegeService.removeById(id));
        return R.ok("删除成功");
    }
}
