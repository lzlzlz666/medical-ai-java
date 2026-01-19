package com.lz.service;

import com.lz.dto.HealthRecordDTO;
import com.lz.result.Result;

public interface HealthRecordService {
    Result insert(HealthRecordDTO healthRecordDTO);

    Result update(HealthRecordDTO healthRecordDTO);
}
