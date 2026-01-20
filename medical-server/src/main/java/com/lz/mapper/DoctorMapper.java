package com.lz.mapper;

import com.lz.entity.Admin;
import com.lz.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DoctorMapper {

    @Select("select * from doctor where username = #{username}")
    Doctor getByUsername(String username);

    @Select("select * from doctor where id = #{doctorId}")
    Doctor list(Long doctorId);
}
