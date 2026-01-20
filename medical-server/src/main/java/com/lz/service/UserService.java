package com.lz.service;

import com.lz.dto.UserDTO;
import com.lz.dto.UserLoginDTO;
import com.lz.dto.UserPasswordDTO;
import com.lz.dto.UserRegisterDTO;
import com.lz.entity.User;
import com.lz.result.Result;
import com.lz.vo.UserVO;

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
     * @param
     * @return
     */
    Result<User> getById();

    /**
     * 获取该用户的信息与当日的健康信息
     * @return
     */
    Result<UserVO> getUserWithTodayHealth();

    Result update(UserDTO userDTO);

    Result updatePassword(UserPasswordDTO userPasswordDTO);
}
