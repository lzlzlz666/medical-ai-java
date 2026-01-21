package com.lz.service;

import com.lz.vo.AdminDashboardVO;

import java.time.LocalDate;

public interface ReportService {
    AdminDashboardVO getDashboardStatistics(LocalDate begin, LocalDate end);
}
