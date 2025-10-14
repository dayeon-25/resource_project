package com.example.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvgDTO {
    private double plasticAvg;
    private double vinylAvg;
    private double woodAvg;
}
