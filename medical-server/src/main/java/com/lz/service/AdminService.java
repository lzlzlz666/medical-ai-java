package com.lz.service;

import com.lz.dto.AdminLoginDTO;
import com.lz.entity.Admin;
import com.lz.result.Result;

public interface AdminService {
    Admin login(AdminLoginDTO adminLoginDTO);

    Result<Admin> list();
}
