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

	public BoxService(BoxRepository boxRepository, WebClient.Builder webClientBuilder, UserService userService) {
		this.boxRepository = boxRepository;
		this.webClientBuilder = webClientBuilder;
		this.userService = userService;
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
		Optional<Box> optionalBox  = boxRepository.findByName(name);
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
			IOTResponseDTO response = webClient
					.post()
					.uri(uri)
					.retrieve()
					.bodyToMono(IOTResponseDTO.class)
					.timeout(Duration.ofSeconds(60))
					.onErrorResume(TimeoutException.class, t -> Mono.just(new IOTResponseDTO("Fail")))
					.onErrorResume(ConnectException.class,
							t -> Mono.just(new IOTResponseDTO("Fail")))
					.block();

			return response;
		} else if (controll.equals("boxClose")) {
			// 문 닫기 요청 → 사진 및 수거 로그도 포함된 응답 받기
			System.out.println("문닫기 요청");
			CloseBoxResponseDTO response = webClient
					.post()
					.uri(uri)
					.retrieve()
					.bodyToMono(CloseBoxResponseDTO.class)
					.timeout(Duration.ofSeconds(60))
		            .block();
			if(response.getStatus().equals("200")) {
				
			} else {
				return null;
			}
			// Box 수거 상태 업데이트
			// 로그 작성
			// 사진파일 저장
			//saveFile(response.getImage());

			return response;
		} else {
			return "Fail";
		}
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
