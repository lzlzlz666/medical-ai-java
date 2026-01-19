package com.lz.mapper;

import com.lz.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    void insert(User user);

    @Select("select * from user where username = #{username}")
    User getByUsername(String username);

    @Select("select * from user where id = #{id}")
    User getById(Long id);
}
