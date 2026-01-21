package com.lz.service.impl;

import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminEditDTO;
import com.lz.dto.AdminLoginDTO;
import com.lz.dto.AdminPasswordDTO;
import com.lz.entity.Admin;
import com.lz.entity.User;
import com.lz.exception.AccountLockedException;
import com.lz.exception.AccountNotFoundException;
import com.lz.exception.PasswordErrorException;
import com.lz.mapper.AdminMapper;
import com.lz.result.Result;
import com.lz.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
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

    /**
     * 修改个人基础信息 (昵称、头像)
     */
    public void updateProfile(AdminEditDTO dto) {
        Long adminId = BaseContext.getCurrentId();

        Admin admin = Admin.builder()
                .id(adminId)
                .nickname(dto.getNickname())
                .avatar(dto.getAvatar()) // 如果你的数据库没有 avatar 字段，记得加上
                .build();

        adminMapper.update(admin);
    }

    /**
     * 修改密码
     * @param dto
     */
    public void editPassword(AdminPasswordDTO dto) {
        Long adminId = BaseContext.getCurrentId();
        // 1. 根据ID查询当前用户数据
        Admin admin = adminMapper.getById(adminId);
        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 对前端传来的旧密码进行 MD5 加密
        String oldPasswordMd5 = DigestUtils.md5DigestAsHex(dto.getOldPassword().getBytes());

        // 3. 比对数据库中的密码
        if (!oldPasswordMd5.equals(admin.getPassword())) {
            // 抛出异常，由全局异常处理器捕获返回给前端
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 如果比对成功，加密新密码并更新
        String newPasswordMd5 = DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes());
        admin.setPassword(newPasswordMd5);
        adminMapper.update(admin);
    }
}



