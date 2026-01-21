package com.lz.controller.admin;

import com.lz.dto.DepartmentDTO;
import com.lz.dto.DepartmentPageQueryDTO;
import com.lz.entity.Department;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("departmentAdminRestController")
@Slf4j
@RequestMapping("/admin/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 科室分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(DepartmentPageQueryDTO departmentPageQueryDTO) {
        log.info("科室分页查询: {}", departmentPageQueryDTO);
        PageResult pageResult = departmentService.pageQuery(departmentPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增科室
     */
    @PostMapping
    public Result save(@RequestBody DepartmentDTO departmentDTO) {
        log.info("新增科室: {}", departmentDTO);
        departmentService.save(departmentDTO);
        return Result.success();
    }

    /**
     * 根据ID查询科室（用于回显）
     */
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        log.info("根据ID查询科室: {}", id);
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    /**
     * 修改科室
     */
    @PutMapping
    public Result update(@RequestBody DepartmentDTO departmentDTO) {
        log.info("修改科室: {}", departmentDTO);
        departmentService.update(departmentDTO);
        return Result.success();
    }

    /**
     * 删除科室
     */
    @DeleteMapping
    public Result delete(@RequestParam Long id) {
        log.info("删除科室: {}", id);
        departmentService.deleteById(id);
        return Result.success();
    }

    /**
     * 获取所有科室下拉列表 (新增医生时使用)
     */
    @GetMapping("/list")
    public Result<List<Department>> list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

}
