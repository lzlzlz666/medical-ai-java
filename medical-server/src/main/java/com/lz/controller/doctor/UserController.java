package com.lz.controller.doctor;

import com.lz.dto.ConsultationPageQueryDTO;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("doctorUserRestController")
@Slf4j
@RequestMapping("/doctor/user")
public class UserController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 患者端分页查询会话列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/page")
    public Result<PageResult> page(@RequestBody ConsultationPageQueryDTO queryDTO) {
        log.info("分页查询患者咨询申请: {}", queryDTO);
        PageResult pageResult = consultationService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 处理患者咨询
     * @param processId  // 2是接收，3是拒绝
     * @return
     */
    @PutMapping("/process")
    public Result processConsultation(Integer processId, Long userId) {
        return consultationService.changeStatus(processId, userId);
    }
}
