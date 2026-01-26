package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MedicalReportVO implements Serializable {
    private String title;
    private Double score; // 满分100
    private List<String> suggestions;
}
