package com.lz.controller.admin;


import com.lz.result.Result;
import com.lz.service.ReportService;
import com.lz.vo.AdminDashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController("AdminDashboardRestController")
@RequestMapping("/admin/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取仪表盘统计数据 (包含：医生数、科室数、用户增长图表数据)
     * @param type 统计时间范围: "last7Days" (默认) 或 "last30Days"
     */
    @GetMapping("/statistics")
    public Result<AdminDashboardVO> getDashboardStatistics(
            @RequestParam(required = false) String type
    ) {
        log.info("获取仪表盘数据, 时间范围: {}", type);

        LocalDate begin;
        LocalDate end = LocalDate.now();

        // 计算时间范围
        if ("last7Days".equals(type)) {
            begin = end.minusDays(6); // 近7天
        } else {
            begin = end.minusDays(29); // 默认近30天
        }

        AdminDashboardVO adminDashboardVO = reportService.getDashboardStatistics(begin, end);
        return Result.success(adminDashboardVO);
    }

}
