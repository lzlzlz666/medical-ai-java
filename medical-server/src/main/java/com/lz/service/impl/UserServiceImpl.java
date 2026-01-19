package com.lz.service.impl;

import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.dto.UserLoginDTO;
import com.lz.dto.UserRegisterDTO;
import com.lz.entity.User;
import com.lz.exception.AccountAlreadyExistException;
import com.lz.exception.PasswordErrorException;
import com.lz.mapper.UserMapper;
import com.lz.result.Result;
import com.lz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.lz.exception.AccountNotFoundException;
import com.lz.exception.AccountLockedException;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册用户
     * @param userRegisterDTO
     * @return
     */
    public Result register(UserRegisterDTO userRegisterDTO) {
        String username = userRegisterDTO.getUsername();
        User user = userMapper.getByUsername(username);
        if (user != null) {
            throw new AccountAlreadyExistException(MessageConstant.ACCOUNT_ALREADY_EXIST);
        }
        // 将用户存储到数据库中，密码做加密处理
        String password = userRegisterDTO.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(md5Password)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return user;
    }

    /**
     * 根据id获得用户
     * @param id
     * @return
     */
    public Result<User> getById(Long id) {
        User user = userMapper.getById(id);
        return Result.success(user);
    }
}
