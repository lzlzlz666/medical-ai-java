package com.lz.mapper;

import com.lz.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);

    @Select("select * from admin where id = #{adminId}")
    Admin getById(Long adminId);
}
