package com.lz.mapper;

import com.github.pagehelper.Page;
import com.lz.annotation.AutoFill;
import com.lz.dto.DepartmentPageQueryDTO;
import com.lz.entity.Department;
import com.lz.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    Page<Department> pageQuery(DepartmentPageQueryDTO dto);

    @AutoFill(value = OperationType.INSERT, fillUser = true)
    void insert(Department department);

    @Delete("delete from department where id = #{id}")
    void deleteById(Long id);

    @AutoFill(value = OperationType.UPDATE, fillUser = true)
    void update(Department department);

    @Select("select * from department where id = #{id}")
    Department getById(Long id);

    @Select("select * from department")
    List<Department> list();

    @Select("select count(id) from department")
    Integer count();
}
