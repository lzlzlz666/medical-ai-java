package com.lz.service.impl;

import com.lz.context.BaseContext;
import com.lz.dto.HealthRecordDTO;
import com.lz.entity.HealthRecord;
import com.lz.mapper.HealthRecordMapper;
import com.lz.result.Result;
import com.lz.service.HealthRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HealthRecordImpl implements HealthRecordService {

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    /**
     * 新增一条健康记录数据
     * @param healthRecordDTO
     * @return
     */
    public Result insert(HealthRecordDTO healthRecordDTO) {
        HealthRecord healthRecord = new HealthRecord();
        BeanUtils.copyProperties(healthRecordDTO, healthRecord);

        Long userId = BaseContext.getCurrentId();
        LocalDate today = LocalDate.now();

        healthRecord.setRecordDate(today);
        healthRecord.setUserId(userId);
        healthRecordMapper.insert(healthRecord);
        return Result.success("今日健康指标已导入");
    }

    /**
     * 更新健康记录数据
     * @param healthRecordDTO
     * @return
     */
    public Result update(HealthRecordDTO healthRecordDTO) {
        HealthRecord healthRecord = new HealthRecord();
        BeanUtils.copyProperties(healthRecordDTO, healthRecord);

        Long userId = BaseContext.getCurrentId();
        LocalDate today = LocalDate.now();

        healthRecord.setRecordDate(today);
        healthRecord.setUserId(userId);
        healthRecordMapper.update(healthRecord);
        return Result.success("今日健康指标已更新");
    }
}
