package com.lz.medicalserver;

import com.lz.app.MedicalApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class MedicalServerApplicationTests {

    @Resource
    private MedicalApp medicalApp;

    @Test
    void contextLoads() {
    }

}
