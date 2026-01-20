package com.lz.service;

import com.lz.dto.DoctorLoginDTO;
import com.lz.entity.Doctor;
import com.lz.result.Result;
import com.lz.vo.DoctorVO;

public interface DoctorService {

    Doctor login(DoctorLoginDTO doctorLoginDTO);

    Result<DoctorVO> list();
}
