package com.lz.service.impl;

import com.lz.mapper.DepartmentMapper;
import com.lz.mapper.DoctorMapper;
import com.lz.mapper.UserMapper;
import com.lz.service.ReportService;
import com.lz.vo.AdminDashboardVO;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 获取仪表盘统计数据 (包含：医生数、科室数、用户增长图表数据)
     */
    public AdminDashboardVO getDashboardStatistics(LocalDate begin, LocalDate end) {
        // 1. === 查询静态统计数据 (医生数、科室数) ===
        Integer doctorCount = doctorMapper.countEnabledDoctors();
        Integer deptCount = departmentMapper.count();

        // 2. === 处理图表数据 (用户增长趋势) ===
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();   // 新增用户数
        List<Integer> totalUserList = new ArrayList<>(); // 总用户数

        // 遍历每一天查询数据库 (注意：如果数据量巨大，实际生产中会用聚合SQL优化，这里用循环逻辑最清晰)
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // A. 查询当日新增用户
            Map<String, Object> mapNew = new HashMap<>();
            mapNew.put("begin", beginTime);
            mapNew.put("end", endTime);
            Integer newUserCount = userMapper.countUserByMap(mapNew);
            newUserList.add(newUserCount);

            // B. 查询截止当日总用户
            Map<String, Object> mapTotal = new HashMap<>();
            mapTotal.put("end", endTime);
            Integer totalUserCount = userMapper.countUserByMap(mapTotal);
            totalUserList.add(totalUserCount);
        }

        // 3. === 封装并返回 ===
        return AdminDashboardVO.builder()
                .doctorCount(doctorCount)
                .deptCount(deptCount)
                .dateList(String.join(",", dateList.stream()
                        .map(LocalDate::toString).toList()))
                .newUserList(String.join(",", newUserList.stream()
                        .map(String::valueOf).toList()))
                .totalUserList(String.join(",", totalUserList.stream()
                        .map(String::valueOf).toList()))
                .build();
    }
}
