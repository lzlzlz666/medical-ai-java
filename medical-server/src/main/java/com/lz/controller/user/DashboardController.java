package com.lz.controller.user;

import com.lz.result.Result;
import com.lz.service.HealthRecordService;
import com.lz.vo.HealthDashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * 根据type获取每周、每月、每年的健康信息
     * 统计范围类型：last7Days (近7天), month (本月)
     * @param type
     * @return
     */
    @GetMapping("/statistics")
    public Result<HealthDashboardVO> list(String type) {
        return healthRecordService.list(type);
    }
}
