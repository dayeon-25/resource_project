package com.example.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private AvgDTO avg;

    private double suitableRatio;
    private double unsuitableRatio;

    private List<RaUsageDTO> usageList;
    private List<RaPurposeDTO> purposeList;
    private List<RaRegionDTO> regionList;
}
