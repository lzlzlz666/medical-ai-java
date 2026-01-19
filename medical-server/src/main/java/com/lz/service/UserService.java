package com.lz.service;

import com.lz.dto.UserLoginDTO;
import com.lz.dto.UserRegisterDTO;
import com.lz.entity.User;
import com.lz.result.Result;

public interface UserService {
    /**
     * 注册用户
     * @param userRegisterDTO
     * @return
     */
    Result register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 根据id获得用户
     * @param id
     * @return
     */
    Result<User> getById(Long id);
}
