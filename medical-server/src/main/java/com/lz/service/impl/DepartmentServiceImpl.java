package com.lz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lz.constant.MessageConstant;
import com.lz.dto.DepartmentDTO;
import com.lz.dto.DepartmentPageQueryDTO;
import com.lz.entity.Department;
import com.lz.entity.Doctor;
import com.lz.exception.DeletionNotAllowedException;
import com.lz.mapper.DepartmentMapper;
import com.lz.mapper.DoctorMapper;
import com.lz.result.PageResult;
import com.lz.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private DoctorMapper doctorMapper;


    @Override
    public PageResult pageQuery(DepartmentPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<Department> page = departmentMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void save(DepartmentDTO dto) {
        Department department = new Department();
        BeanUtils.copyProperties(dto, department);
        department.setCreateTime(LocalDateTime.now());
        department.setUpdateTime(LocalDateTime.now());
        departmentMapper.insert(department);
    }

    @Override
    public Department getById(Long id) {
        return departmentMapper.getById(id);
    }

    @Override
    public void update(DepartmentDTO dto) {
        Department department = new Department();
        BeanUtils.copyProperties(dto, department);
        department.setUpdateTime(LocalDateTime.now());
        departmentMapper.update(department);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        //先检查该科室下是否有医生，如果有则不允许删除
        List<Doctor> doctorList = doctorMapper.getByDeptId(id);
        if (doctorList != null && !doctorList.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DEPARTMENT_BE_RELATED_BY_DOCTOR);
        }
        departmentMapper.deleteById(id);
    }

    /**
     * 获取所有部门信息
     * @return
     */
    public List<Department> list() {
        return departmentMapper.list();
    }
}
