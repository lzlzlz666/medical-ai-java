package com.lz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lz.constant.ImageConstant;
import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.context.BaseContext;
import com.lz.dto.*;
import com.lz.entity.HealthRecord;
import com.lz.entity.User;
import com.lz.exception.*;
import com.lz.mapper.HealthRecordMapper;
import com.lz.mapper.UserMapper;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.UserService;
import com.lz.vo.HealthVO;
import com.lz.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HealthRecordMapper healthRecordMapper;

    /**
     * 注册用户
     * @param userRegisterDTO
     * @return
     */
    @Transactional
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
                .nickname("您还未设置昵称")
                .avatar(ImageConstant.IMAGE_DEFAULT)
                .status(1)
                .gender(1)
                .isSmoker(0)
                .isDrinker(0)
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
     * @param
     * @return
     */
    public Result<User> getById() {
        Long id = BaseContext.getCurrentId();
        User user = userMapper.getById(id);
        return Result.success(user);
    }

    /**
     * 获取该用户的信息与当日的健康信息
     * @return
     */
    @Transactional
    public Result<UserVO> getUserWithTodayHealth() {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        UserVO userVO = new UserVO();
        HealthVO todayHealthVO = new HealthVO();
        // 将user属性赋值给userVO
        BeanUtils.copyProperties(user, userVO);

        // 直接查今天的记录（DATE）
        HealthRecord todayRecord = healthRecordMapper.getByUserIdAndRecordDate(userId, LocalDate.now());
        if (todayRecord == null) {
            userVO.setTodayCompleted(false);
        } else {
            userVO.setTodayCompleted(true);
            BeanUtils.copyProperties(todayRecord, todayHealthVO);
        }
        userVO.setTodayHealth(todayHealthVO);

        return Result.success(userVO);
    }

    /**
     * 修改用户信息
     * @param userDTO
     * @return
     */
    public Result update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO , user);
        Long userId = BaseContext.getCurrentId();
        user.setId(userId);
        userMapper.update(user);
        return Result.success("用户基本信息更新成功");
    }

    /**
     * 用户修改密码
     * @param userPasswordDTO
     * @return
     */
    @Transactional
    public Result updatePassword(UserPasswordDTO userPasswordDTO) {
        Long userId = BaseContext.getCurrentId();
        String oldPassword = userPasswordDTO.getOldPassword();
        String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        User user = userMapper.getById(userId);
        if (!md5OldPassword.equals(user.getPassword())) {
            return Result.error(MessageConstant.OLD_PASSWORD_ERROR);
        }

        String newPassword = userPasswordDTO.getNewPassword();
        String md5NewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        user = User.builder()
                .id(userId)
                .password(md5NewPassword).build();
        userMapper.update(user);
        return Result.success("修改密码成功");
    }

    /**
     * 分页查询
     */
    public PageResult pageQuery(UserPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<User> page = userMapper.pageQuery(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用禁用账号
     */
    public void startOrStop(Integer status, Long id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        userMapper.update(user);
    }

    @Override
    public void resetPassword(Long id) {
        // 假设默认密码是 123456
        String defaultPassword = "123456";
        // 进行 MD5 加密
        String password = DigestUtils.md5DigestAsHex(defaultPassword.getBytes());

        User user = User.builder()
                .id(id)
                .password(password)
                .build();
        userMapper.update(user);
    }
}
