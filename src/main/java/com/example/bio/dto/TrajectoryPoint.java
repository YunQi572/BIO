package com.example.bio.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TrajectoryPoint {
    private LocalDateTime timestamp;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
}