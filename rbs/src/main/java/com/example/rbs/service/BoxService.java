package com.example.rbs.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.rbs.dto.CloseBoxResponseDTO;
import com.example.rbs.dto.IOTResponseDTO;
import com.example.rbs.entity.Box;
import com.example.rbs.repository.BoxRepository;

import reactor.core.publisher.Mono;

@Service
public class BoxService {

	private final BoxRepository boxRepository;
	private final WebClient.Builder webClientBuilder;
	private final UserService userService;
	private final BoxLogService boxLogService;

	public BoxService(BoxRepository boxRepository, WebClient.Builder webClientBuilder, UserService userService,
			BoxLogService boxLogService) {
		this.boxRepository = boxRepository;
		this.webClientBuilder = webClientBuilder;
		this.userService = userService;
		this.boxLogService = boxLogService;
	}

	// 모든 수거함 조회
	public List<Box> findAllBox() {
		return boxRepository.findAll();
	}

	// 수거함 id로 검색
	public Box findBoxById(int id) {
		Optional<Box> optionalBox = boxRepository.findById(id);
		if (optionalBox.isPresent()) {
			return optionalBox.get();
		} else {
			return null;
		}
	}

	// 수거함 이름으로 검색
	public Box findBoxByName(String name) {
		Optional<Box> optionalBox = boxRepository.findByName(name);
		if (optionalBox.isPresent()) {
			return optionalBox.get();
		} else {
			return null;
		}
	}

	// 수거함 제어
	public Object boxControll(String controll, int boxId, int number) {
		Box box = findBoxById(boxId);
		String uri;
		String role = userService.getRole();

		uri = controll + number;

		WebClient webClient = webClientBuilder.baseUrl("http://" + box.getIPAddress()).build();

		if (controll.equals("boxOpen")) {
			IOTResponseDTO response = webClient.post().uri(uri).retrieve().bodyToMono(IOTResponseDTO.class)
					.timeout(Duration.ofSeconds(60))
					.onErrorResume(TimeoutException.class, t -> Mono.just(new IOTResponseDTO("Fail")))
					.onErrorResume(ConnectException.class, t -> Mono.just(new IOTResponseDTO("Fail"))).block();

			return response;
		} else if (controll.equals("boxClose")) {
			// 문 닫기 요청 → 사진 및 수거 로그도 포함된 응답 받기
			System.out.println("문닫기 요청");
			CloseBoxResponseDTO response = webClient.post().uri(uri).retrieve().bodyToMono(CloseBoxResponseDTO.class)
					.timeout(Duration.ofSeconds(60)).block();
			if (response.getStatus().equals("200")) {

			} else {
				return null;
			}
			
			Map<String, Integer> resultMap = response.getResult();
			
			// Box 용량 업데이트
			if (resultMap != null && resultMap.containsKey("battery")) {
			    int batteryCount = resultMap.get("battery");
			    box.setVolume1(box.getVolume1() + batteryCount * 1);
			} else if(resultMap != null && resultMap.containsKey("discharged")) {
				int batteryCount = resultMap.get("discharged");
			    box.setVolume2(box.getVolume2() + batteryCount * 5);
			} else if(resultMap != null && resultMap.containsKey("notDischarged")) {
				int batteryCount = resultMap.get("notDischarged");
			    box.setVolume2(box.getVolume3() + batteryCount * 5);
			}
			
			// 로그 작성 및 사진파일 저장
			boxLogService.logUpdate(boxId, response.getResult(), saveFile(response.getImage()));
			return response;
		} else {
			return "Fail";
		}
	}

	// 수거함 사용 끝
	public int boxEnd(int boxId) {
		int point = boxLogService.boxEnd(boxId);
		userService.updatePoint(point);
		return point;
	}

	private String saveFile(String file) {
		try {
			if (file != null) {
				byte[] imageBytes = Base64.getDecoder().decode(file);

				// 저장 경로 및 파일명 설정
				String uploadDir = "C:/uploads/images/";
				File dir = new File(uploadDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				// 파일 이름: UUID + 확장자
				String fileName = UUID.randomUUID().toString() + "_boxLog.jpg";
				String filePath = fileName;

				// 파일 저장
				File imageFile = new File(filePath);
				try (OutputStream os = new FileOutputStream(imageFile)) {
					os.write(imageBytes);
				}
				return filePath;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
