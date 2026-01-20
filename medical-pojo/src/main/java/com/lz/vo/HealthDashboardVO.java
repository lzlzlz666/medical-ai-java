package com.lz.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HealthDashboardVO implements Serializable {

    private List<HealthVO> healthList;
}
