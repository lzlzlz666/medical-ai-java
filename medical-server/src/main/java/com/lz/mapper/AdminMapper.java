package com.lz.mapper;

import com.lz.annotation.AutoFill;
import com.lz.entity.Admin;
import com.lz.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);

    @Select("select * from admin where id = #{adminId}")
    Admin getById(Long adminId);

    @AutoFill(OperationType.UPDATE)
    void update(Admin admin);
}
