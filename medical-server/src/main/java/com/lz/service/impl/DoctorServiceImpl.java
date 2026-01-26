package com.lz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.constant.MessageConstant;
import com.lz.constant.StatusConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminLoginDTO;
import com.lz.dto.DoctorDTO;
import com.lz.dto.DoctorLoginDTO;
import com.lz.dto.DoctorPageQueryDTO;
import com.lz.entity.Admin;
import com.lz.entity.ConsultationSession;
import com.lz.entity.Department;
import com.lz.entity.Doctor;
import com.lz.exception.AccountLockedException;
import com.lz.exception.AccountNotFoundException;
import com.lz.exception.PasswordErrorException;
import com.lz.mapper.ConsultationMapper;
import com.lz.mapper.DepartmentMapper;
import com.lz.mapper.DoctorMapper;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import com.lz.service.DoctorService;
import com.lz.vo.DoctorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private ConsultationMapper consultationMapper;

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

    /**
     * 医生分页查询
     * @param doctorPageQueryDTO
     * @return
     */
    public PageResult page(DoctorPageQueryDTO doctorPageQueryDTO) {
        // 1. 设置分页参数
        PageHelper.startPage(doctorPageQueryDTO.getPage(), doctorPageQueryDTO.getPageSize());

        // 2. 执行查询 (此时返回的是 PageHelper 代理后的 List)
        List<DoctorVO> list = doctorMapper.page(doctorPageQueryDTO);

        // 3. ✅ 核心修正：使用 PageInfo 解析分页结果
        // PageInfo 会自动计算 total、pages 等数据，比直接强转 Page 更稳健
        PageInfo<DoctorVO> pageInfo = new PageInfo<>(list);

        // 4. 返回结果
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void save(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(dto, doctor);

        // 默认设置
        doctor.setStatus(1); // 默认启用
        doctor.setWorkStatus(0); // 默认离线
        // 默认密码 123456 (MD5加密)
        doctor.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        doctorMapper.insert(doctor);
    }

    @Override
    public DoctorVO getById(Long id) {
        // 这里直接复用 pageQuery 的关联逻辑或者单表查询均可
        // 为了简单，通常单表查询回显即可，前端会根据 deptId 自动匹配下拉框
        Doctor doctor = doctorMapper.getById(id);
        DoctorVO vo = new DoctorVO();
        BeanUtils.copyProperties(doctor, vo);
        return vo;
    }

    @Override
    public void update(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(dto, doctor);
        doctorMapper.update(doctor);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Doctor doctor = Doctor.builder()
                .id(id)
                .status(status)
                .build();
        doctorMapper.update(doctor);
    }

    /**
     * 根据doctorId申请医生介入审核
     * @param doctorId
     * @return
     */
    @Transactional
    public Result applyDoctor(Long doctorId) {
        Doctor doctor = doctorMapper.getById(doctorId);
        Integer dailyAudit = doctor.getMaxDailyAudit();
        Integer status = doctor.getStatus();
        Integer workStatus = doctor.getWorkStatus();
        if (dailyAudit <= 0 || status != 1 || workStatus != 1) {
            return Result.error(MessageConstant.APPLY_NOT_ALLOW);
        }
        Long userId = BaseContext.getCurrentId();
        doctor.setMaxDailyAudit(dailyAudit - 1);
        doctorMapper.updateByUser(doctor);

        // 插入 consultation_session 表中
        ConsultationSession session = new ConsultationSession();
        session.setUserId(userId);
        session.setDoctorId(doctorId);
        session.setStatus(1);
        session.setAiSummary("咨询慢性病");
        consultationMapper.insertConsultation(session);
        return Result.success("申请专家成功！");
    }
}
