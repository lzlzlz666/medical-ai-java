package com.lz.controller.user;

import com.lz.constant.JwtClaimsConstant;
import com.lz.context.BaseContext;
import com.lz.dto.UserDTO;
import com.lz.dto.UserLoginDTO;
import com.lz.dto.UserPasswordDTO;
import com.lz.dto.UserRegisterDTO;
import com.lz.entity.User;
import com.lz.properties.JwtProperties;
import com.lz.result.Result;
import com.lz.service.UserService;
import com.lz.utils.JwtUtil;
import com.lz.vo.UserLoginVO;
import com.lz.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Validated UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("员工登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .token(token)
                .build();

        return Result.success(userLoginVO);
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
     * 根据id查询用户
     * @param
     * @return
     */
    @GetMapping
    public Result<User> getById() {
        return userService.getById();
    }

    /**
     * 获取该用户的信息与当日的健康信息
     * @return
     */
    @GetMapping("/profile")
    public Result<UserVO> getUserWithTodayHealth() {
        return userService.getUserWithTodayHealth();
    }

    /**
     * 更新用户基本信息
     * @param userDTO
     * @return
     */
    @PutMapping()
    public Result update(@RequestBody UserDTO userDTO) {
        return userService.update(userDTO);
    }

    @PutMapping("/password")
    public Result updatePassword(@RequestBody UserPasswordDTO userPasswordDTO) {
        return userService.updatePassword(userPasswordDTO);
    }
}
