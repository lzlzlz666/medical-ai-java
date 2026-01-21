package com.lz.controller.admin;

import com.lz.dto.DoctorDTO;
import com.lz.dto.DoctorPageQueryDTO;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.DoctorService;
import com.lz.vo.DoctorVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("doctorAdminController")
@Slf4j
@RequestMapping("/admin/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 医生分页查询 (关联科室表)
     */
    @GetMapping("/page")
    public Result<PageResult> page(DoctorPageQueryDTO doctorPageQueryDTO) {
        log.info("医生分页查询，参数为{}", doctorPageQueryDTO);
        PageResult pageResult = doctorService.page(doctorPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增医生
     */
    @PostMapping
    public Result save(@RequestBody DoctorDTO doctorDTO) {
        log.info("新增医生: {}", doctorDTO);
        doctorService.save(doctorDTO);
        return Result.success();
    }

    /**
     * 根据ID查询医生 (用于回显)
     */
    @GetMapping("/{id}")
    public Result<DoctorVO> getById(@PathVariable Long id) {
        log.info("根据ID查询医生: {}", id);
        DoctorVO doctorVO = doctorService.getById(id);
        return Result.success(doctorVO);
    }

    /**
     * 修改医生信息
     */
    @PutMapping
    public Result update(@RequestBody DoctorDTO doctorDTO) {
        log.info("修改医生信息: {}", doctorDTO);
        doctorService.update(doctorDTO);
        return Result.success();
    }

    /**
     * 启用/禁用医生账号
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用医生账号: {}, {}", status, id);
        doctorService.startOrStop(status, id);
        return Result.success();
    }
}
