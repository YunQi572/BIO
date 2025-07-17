package com.example.bio.controller;

import com.example.bio.dto.TrajectoryAnalysisRequest;
import com.example.bio.dto.TrajectoryAnalysisResponse;
import com.example.bio.service.TrajectoryAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analysis")
public class TrajectoryAnalysisController {

    @Autowired
    private TrajectoryAnalysisService trajectoryAnalysisService;

    @PostMapping("/trajectory")
    public ResponseEntity<TrajectoryAnalysisResponse> analyzeTrajectory(
            @RequestBody TrajectoryAnalysisRequest request) {
        TrajectoryAnalysisResponse response = trajectoryAnalysisService.analyzeTrajectory(request);
        return ResponseEntity.ok(response);
    }
}