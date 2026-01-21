package com.lz.controller.doctor;

import com.lz.constant.JwtClaimsConstant;
import com.lz.context.BaseContext;
import com.lz.dto.AdminLoginDTO;
import com.lz.dto.DoctorLoginDTO;
import com.lz.dto.DoctorPageQueryDTO;
import com.lz.entity.Admin;
import com.lz.entity.Doctor;
import com.lz.properties.JwtProperties;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.AdminService;
import com.lz.service.DoctorService;
import com.lz.utils.JwtUtil;
import com.lz.vo.AdminLoginVO;
import com.lz.vo.DoctorLoginVO;
import com.lz.vo.DoctorVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("doctorDoctorRestController")
@Slf4j
@RequestMapping("/doctor/doctor")
public class DoctorController {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DoctorService doctorService;

    /**
     * 管理员登录
     * @param doctorLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<DoctorLoginVO> login(@RequestBody DoctorLoginDTO doctorLoginDTO) {
        log.info("医生登录：{}", doctorLoginDTO);

        Doctor doctor = doctorService.login(doctorLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.DOCTOR_ID, doctor.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getDoctorSecretKey(),
                jwtProperties.getDoctorTtl(),
                claims);

        DoctorLoginVO doctorLoginVO = DoctorLoginVO.builder()
                .id(doctor.getId())
                .username(doctor.getUsername())
                .realName(doctor.getRealName())
                .avatar(doctor.getAvatar())
                .token(token)
                .build();

        return Result.success(doctorLoginVO);
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
     * 根据id查询医生信息
     * @return
     */
    @GetMapping
    public Result<DoctorVO> list() {
        return doctorService.list();
    }

}
