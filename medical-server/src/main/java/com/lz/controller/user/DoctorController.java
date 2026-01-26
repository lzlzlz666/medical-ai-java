package com.lz.controller.user;

import com.lz.dto.DoctorPageQueryDTO;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userDoctorRestController")
@Slf4j
@RequestMapping("/user/doctor")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    /**
     * 分页查询医生信息
     * @param doctorPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DoctorPageQueryDTO doctorPageQueryDTO) {
        log.info("医生分页查询，参数为{}", doctorPageQueryDTO);
        PageResult pageResult = doctorService.page(doctorPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/apply/{doctorId}")
    public Result apply(@PathVariable Long doctorId) {
        return doctorService.applyDoctor(doctorId);
    }
}
