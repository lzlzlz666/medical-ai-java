package com.lz.controller.user;

import com.lz.dto.HealthRecordDTO;
import com.lz.result.Result;
import com.lz.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user/healthRecord")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * 新增当日健康记录
     * @param healthRecordDTO
     * @return
     */
    @PostMapping
    public Result add(@RequestBody HealthRecordDTO healthRecordDTO) {
        return healthRecordService.insert(healthRecordDTO);
    }

    @PutMapping
    public Result update(@RequestBody HealthRecordDTO healthRecordDTO) {
        return healthRecordService.update(healthRecordDTO);
    }
}
