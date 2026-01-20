package com.lz.service;

import com.lz.dto.HealthRecordDTO;
import com.lz.result.Result;
import com.lz.vo.HealthDashboardVO;

public interface HealthRecordService {
    Result insert(HealthRecordDTO healthRecordDTO);

    Result update(HealthRecordDTO healthRecordDTO);

    Result<HealthDashboardVO> list(String type);
}
