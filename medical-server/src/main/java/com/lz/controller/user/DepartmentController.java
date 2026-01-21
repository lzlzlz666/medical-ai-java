package com.lz.controller.user;

import com.lz.entity.Department;
import com.lz.result.Result;
import com.lz.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有部门信息
     * @return
     */
    @GetMapping
    public Result<List<Department>> list() {
        List<Department> departmentList = departmentService.list();
        return Result.success(departmentList);
    }
}
