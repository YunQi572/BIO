package com.example.bio.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrajectoryAnalysisRequest {
    private String species;
    private String animalId;
    private List<TrajectoryPoint> trajectoryPoints;
}