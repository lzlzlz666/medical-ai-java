package com.lz.service.impl;

import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminLoginDTO;
import com.lz.entity.Admin;
import com.lz.entity.User;
import com.lz.exception.AccountLockedException;
import com.lz.exception.AccountNotFoundException;
import com.lz.exception.PasswordErrorException;
import com.lz.mapper.AdminMapper;
import com.lz.result.Result;
import com.lz.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Admin admin = adminMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (admin == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(admin.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (admin.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return admin;
    }

    @Override
    public Result<Admin> list() {
        Long adminId = BaseContext.getCurrentId();
        Admin admin = adminMapper.getById(adminId);

        return Result.success(admin);
    }
}
