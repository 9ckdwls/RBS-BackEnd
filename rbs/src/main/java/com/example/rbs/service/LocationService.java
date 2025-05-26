package com.example.rbs.service;

import com.example.rbs.entity.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Value("${flask.url.base}")
    private String flaskBaseUrl;

    @Value("${flask.url.upload:/upload-multiple}")
    private String flaskUploadPath;

    @Value("${flask.url.compare:/recommend/compare}")
    private String flaskComparePath;

    @Value("${flask.url.coordinates:/get-coordinates}")
    private String flaskCoordinatesPath;

    private final RestTemplate restTemplate;
    private final BoxService boxService;

    public LocationService(BoxService boxService) {
        this.boxService = boxService;
        this.restTemplate = new RestTemplate();
    }

    private String getFlaskUrl(String path) {
        return flaskBaseUrl + path;
    }

    public String uploadFilesToFlask(Map<String, MultipartFile> files) throws IOException {
        logger.info("📁 Flask로 전송할 파일 수: {}", files.size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
            String key = entry.getKey();
            MultipartFile file = entry.getValue();

            logger.info("📨 업로드 받은 파일 - key: {}, filename: {}", key, file.getOriginalFilename());

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add(key, resource); // ✔️ key에 맞게 Flask로 전송
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                getFlaskUrl(flaskUploadPath), requestEntity, String.class
        );

        logger.info("✅ Flask 응답: {}", flaskResponse.getBody());
        return flaskResponse.getBody();
    }


    public List<?> getFilteredRecommendedBoxes() {
        // 1. 모든 수거함 데이터 가져오기
        List<Box> allBoxes = boxService.findAllBox();
        logger.info("기존 수거함 데이터 조회 완료: {}개", allBoxes.size());

        // 2. Flask 서버로 수거함 데이터 전송하고 필터링된 추천 위치 받기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 수거함 데이터를 요청 본문에 포함
        HttpEntity<List<Box>> requestEntity = new HttpEntity<>(allBoxes, headers);

        // Flask 서버로 POST 요청 보내기
        ResponseEntity<List> response = restTemplate.postForEntity(
                getFlaskUrl(flaskComparePath), requestEntity, List.class
        );

        // 응답 헤더에서 삭제된 위치 수 확인 (있는 경우)
        String removedLocations = response.getHeaders().getFirst("X-Removed-Locations");
        if (removedLocations != null) {
            logger.info("기존 수거함과 가까워서 제외된 추천 위치: {}개", removedLocations);
        }

        List<?> filteredBoxes = response.getBody();
        logger.info("필터링된 추천 위치 수: {}개", filteredBoxes != null ? filteredBoxes.size() : 0);

        return filteredBoxes;
    }

    public Map<String, Object> getCoordinates() {
        logger.info("소방서 및 어린이보호구역 좌표 데이터 요청 시작");

        // Flask 서버에 좌표 데이터 요청 (GET 메서드)
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getFlaskUrl(flaskCoordinatesPath), Map.class
        );

        Map<String, Object> coordinates = response.getBody();
        logger.info("좌표 데이터 요청 성공: 소방서 {}개, 어린이보호구역 {}개",
                ((List<?>) coordinates.get("fireStations")).size(),
                ((List<?>) coordinates.get("safetyZones")).size());

        return coordinates;
    }
}