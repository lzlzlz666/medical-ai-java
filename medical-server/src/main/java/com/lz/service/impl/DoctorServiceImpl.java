package com.lz.service.impl;

import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminLoginDTO;
import com.lz.dto.DoctorLoginDTO;
import com.lz.entity.Admin;
import com.lz.entity.Department;
import com.lz.entity.Doctor;
import com.lz.exception.AccountLockedException;
import com.lz.exception.AccountNotFoundException;
import com.lz.exception.PasswordErrorException;
import com.lz.mapper.DepartmentMapper;
import com.lz.mapper.DoctorMapper;
import com.lz.result.Result;
import com.lz.service.DoctorService;
import com.lz.vo.DoctorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 医生登录
     * @param doctorLoginDTO
     * @return
     */
    public Doctor login(DoctorLoginDTO doctorLoginDTO) {
        String username = doctorLoginDTO.getUsername();
        String password = doctorLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Doctor doctor = doctorMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (doctor == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(doctor.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (doctor.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return doctor;
    }

    @Override
    public Result<DoctorVO> list() {
        Long doctorId = BaseContext.getCurrentId();
        Doctor doctor = doctorMapper.list(doctorId);

        DoctorVO doctorVO = new DoctorVO();
        BeanUtils.copyProperties(doctor, doctorVO);

        Long deptId = doctorVO.getDeptId();
        Department department = departmentMapper.getById(deptId);
        doctorVO.setDeptName(department.getName());
        return Result.success(doctorVO);
    }
}
