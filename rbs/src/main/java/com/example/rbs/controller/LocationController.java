package com.example.rbs.controller;

import com.example.rbs.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFilesToFlask(@RequestParam Map<String, MultipartFile> files) {
        try {
            String response = locationService.uploadFilesToFlask(files);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 처리 실패: " + e.getMessage());
        }
    }


    @PostMapping("/getFilteredRecommendedBoxes")
    public ResponseEntity<?> getFilteredRecommendedBoxes() {
        try {
            return ResponseEntity.ok(locationService.getFilteredRecommendedBoxes());
        } catch (Exception e) {
            logger.error("추천 위치 필터링 실패", e);
            return ResponseEntity.internalServerError()
                    .body("Flask 서버 요청 실패: " + e.getMessage());
        }
    }

    @GetMapping("/getCoordinates")
    public ResponseEntity<?> getCoordinates() {
        try {
            return ResponseEntity.ok(locationService.getCoordinates());
        } catch (Exception e) {
            logger.error("Flask 서버 좌표 데이터 요청 실패", e);
            return ResponseEntity.internalServerError()
                    .body("Flask 서버 요청 실패: " + e.getMessage());
        }
    }
}