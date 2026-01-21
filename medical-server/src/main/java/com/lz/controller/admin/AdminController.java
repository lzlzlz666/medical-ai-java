package com.lz.controller.admin;

import com.lz.constant.JwtClaimsConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminEditDTO;
import com.lz.dto.AdminLoginDTO;
import com.lz.dto.AdminPasswordDTO;
import com.lz.dto.UserLoginDTO;
import com.lz.entity.Admin;
import com.lz.entity.User;
import com.lz.properties.JwtProperties;
import com.lz.result.Result;
import com.lz.service.AdminService;
import com.lz.utils.JwtUtil;
import com.lz.vo.AdminLoginVO;
import com.lz.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/admin")
public class AdminController {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     * @param adminLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("员工登录：{}", adminLoginDTO);

        Admin admin = adminService.login(adminLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .avatar(admin.getAvatar())
                .token(token)
                .build();

        return Result.success(adminLoginVO);
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        BaseContext.removeCurrentId();
        return Result.success("您已退出登录");
    }

    /**
     * 根据id查询管理员信息
     * @return
     */
    @GetMapping
    public Result<Admin> list() {
        return adminService.list();
    }

    /**
     * 修改个人基础信息 (昵称、头像)
     */
    @PutMapping("/profile")
    public Result updateProfile(@RequestBody AdminEditDTO adminEditDTO) {
        log.info("修改个人信息: {}", adminEditDTO);
        // 确保修改的是当前登录用户
        adminService.updateProfile(adminEditDTO);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody AdminPasswordDTO adminPasswordDTO) {
        log.info("修改密码: {}", adminPasswordDTO);
        adminService.editPassword(adminPasswordDTO);
        return Result.success();
    }

}
