package com.lz.task;

import com.lz.constant.DoctorConstant;
import com.lz.mapper.DoctorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 1. 交给 Spring 管理
@Slf4j     // 日志注解 (如果没有lombok，就用 LoggerFactory 手写)
public class DoctorTask {

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 每天 0 点执行一次
     * Cron 表达式格式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDoctorAuditCount() {
        log.info("开始执行定时任务：重置医生每日审核名额 - {}", LocalDateTime.now());

        // 重置为 3
        doctorMapper.resetAllMaxDailyAudit(DoctorConstant.MAX_DAILY_AUDIT);

        log.info("定时任务完成：所有医生名额已重置为 3");
    }
}