package com.lz.mapper;

import com.lz.annotation.AutoFill;
import com.lz.entity.HealthRecord;
import com.lz.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HealthRecordMapper {

    HealthRecord getByUserIdAndRecordDate(Long userId, LocalDate recordDate);

    @AutoFill(OperationType.INSERT)
    void insert(HealthRecord healthRecord);

    @AutoFill(OperationType.UPDATE)
    void update(HealthRecord healthRecord);

    List<HealthRecord> getStatistics(Long userId, String startDate);
}
