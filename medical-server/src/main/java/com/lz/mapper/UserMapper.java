package com.lz.mapper;

import com.github.pagehelper.Page;
import com.lz.annotation.AutoFill;
import com.lz.dto.UserPageQueryDTO;
import com.lz.entity.User;
import com.lz.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @AutoFill(value = OperationType.INSERT)
    void insert(User user);

    @Select("select * from user where username = #{username}")
    User getByUsername(String username);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(User user);

    Page<User> pageQuery(UserPageQueryDTO dto);

    Integer countUserByMap(Map<String, Object> mapNew);
}
