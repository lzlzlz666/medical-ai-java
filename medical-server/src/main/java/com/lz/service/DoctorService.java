package com.lz.service;

import com.lz.dto.DoctorDTO;
import com.lz.dto.DoctorLoginDTO;
import com.lz.dto.DoctorPageQueryDTO;
import com.lz.entity.Doctor;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.vo.DoctorVO;

public interface DoctorService {

    Doctor login(DoctorLoginDTO doctorLoginDTO);

    Result<DoctorVO> list();

    PageResult page(DoctorPageQueryDTO doctorPageQueryDTO);

    void save(DoctorDTO dto);

    DoctorVO getById(Long id);

    void update(DoctorDTO dto);

    void startOrStop(Integer status, Long id);
}
