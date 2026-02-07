package com.lz.task;

import com.lz.mapper.ConsultationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ConsultationTask {

    @Autowired
    private ConsultationMapper consultationMapper;

    /**
     * 任务 3：【新增】清空临时审核表 temp_audit
     * 每天 0 点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearTempAuditTable() {
        log.info("开始执行定时任务：清空临时审核表(temp_audit) - {}", LocalDateTime.now());

        try {
            consultationMapper.clearTempAudit();
            log.info("定时任务完成：temp_audit 表数据已全部清空");
        } catch (Exception e) {
            log.error("定时任务异常：清空 temp_audit 表失败", e);
        }
    }
}
