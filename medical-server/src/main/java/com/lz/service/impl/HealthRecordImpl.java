package com.lz.service.impl;

import com.lz.context.BaseContext;
import com.lz.dto.HealthRecordDTO;
import com.lz.entity.HealthRecord;
import com.lz.mapper.HealthRecordMapper;
import com.lz.result.Result;
import com.lz.service.HealthRecordService;
import com.lz.vo.HealthDashboardVO;
import com.lz.vo.HealthVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthRecordImpl implements HealthRecordService {

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    /**
     * 新增一条健康记录数据
     *
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
     *
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

    /**
     * 根据type获取每周、每月、每年的数据
     *
     * @param type
     * @return
     */
    public Result<HealthDashboardVO> list(String type) {
        // 1. 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 获取当前日期
        LocalDate now = LocalDate.now();
        LocalDate startDate = null;

        // 3. 根据类型计算“起始日期” (自然周期)
        switch (type) {
            case "last7Days":
                // 近七日
                startDate = now.minusDays(6);
                break;
            case "month":
                // 本月：获取本月的第一天 (1号)
                startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                break;
            default:
                return Result.error("不支持的时间类型");
        }

        // 4. 查询数据库
        // SQL ：create_time需要变： >= startDate 即可
        List<HealthRecord> recordList = healthRecordMapper.getStatistics(userId, startDate.toString());

        // 3. 【核心】使用 Stream 流转换格式
        List<HealthVO> voList = recordList.stream()
                .map(record -> {
                    HealthVO vo = new HealthVO();
                    BeanUtils.copyProperties(record, vo);
                    return vo;
                })
                // C. 排序 (以防数据库返回顺序乱了，按日期升序排列)
                .sorted(Comparator.comparing(HealthVO::getRecordDate))
                .toList();

        // 5. 封装返回
        HealthDashboardVO dashboardVO = new HealthDashboardVO();
        dashboardVO.setHealthList(voList);

        return Result.success(dashboardVO);
    }
}
