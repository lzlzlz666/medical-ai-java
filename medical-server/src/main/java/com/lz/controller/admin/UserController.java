package com.lz.controller.admin;

import com.lz.dto.UserPageQueryDTO;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userAdminRestController")
@Slf4j
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户分页查询
     */
    @GetMapping("/page")
    public Result<PageResult> page(UserPageQueryDTO userPageQueryDTO) {
        log.info("用户分页查询: {}", userPageQueryDTO);
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用用户账号
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用用户账号: {}, {}", status, id);
        userService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 重置用户密码 (默认重置为 123456)
     */
    @PutMapping("/resetPassword")
    public Result resetPassword(@RequestParam Long id) {
        log.info("重置用户密码: {}", id);
        userService.resetPassword(id);
        return Result.success();
    }
}
