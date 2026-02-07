package com.lz.task;

import com.lz.constant.DoctorConstant;
import com.lz.entity.ConsultationSession;
import com.lz.mapper.ConsultationMapper;
import com.lz.mapper.DoctorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component // 1. 交给 Spring 管理
@Slf4j     // 日志注解 (如果没有lombok，就用 LoggerFactory 手写)
public class DoctorTask {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ConsultationMapper consultationMapper;

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

    /**
     * 每天 0 点执行一次
     * Cron 表达式格式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDoctorApply() {
        log.info("开始执行定时任务：重置用户申请状态 - {}", LocalDateTime.now());

        // 1. 获取昨天的开始和结束时间
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startTime = LocalDateTime.of(yesterday, LocalTime.MIN); // 昨天 00:00:00
        LocalDateTime endTime = LocalDateTime.of(yesterday, LocalTime.MAX);   // 昨天 23:59:59

        // 2. 查询昨日的记录 (建议加上 status 过滤，只查未处理的)
        List<ConsultationSession> sessions = consultationMapper.getByUpdateTime(startTime, endTime);

        // 3. 遍历并修改状态 (如果你不是批量更新的话)
        if (sessions != null && !sessions.isEmpty()) {
            sessions.forEach(session -> {
                session.setStatus(0); // 重置为初始状态
                consultationMapper.update(session); // 更新
            });
        }

        log.info("定时任务完成：共重置了 {} 条昨日申请记录", sessions == null ? 0 : sessions.size());
    }
}