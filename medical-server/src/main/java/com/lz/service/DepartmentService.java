package com.lz.service;

import com.lz.dto.DepartmentDTO;
import com.lz.dto.DepartmentPageQueryDTO;
import com.lz.entity.Department;
import com.lz.result.PageResult;

import java.util.List;

public interface DepartmentService {

    PageResult pageQuery(DepartmentPageQueryDTO dto);

    void save(DepartmentDTO dto);

    Department getById(Long id);

    void update(DepartmentDTO dto);

    void deleteById(Long id);

    List<Department> list();
}
