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

import org.springframework.beans.factory.annotation.Value;
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
	@Value("${web.server.IP}")
    private String webIP;
	@Value("${battery.volum}")
	private int volum1;
	@Value("${discharged.volum}")
	private int volum2;
	@Value("${notDischarged.volum}")
	private int volum3;

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
			if(number==0) {
				box.setStore1(1);
			} else if(number==1) {
				box.setStore2(1);
			} else if(number==2) {
				box.setStore3(1);
			}
			
			boxRepository.save(box);
			
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
			if(number==0) {
				box.setStore1(0);
			} else if(number==1) {
				box.setStore2(0);
			} else if(number==2) {
				box.setStore3(0);
			}
			boxRepository.save(box);
			
			Map<String, Integer> resultMap = response.getResult();
			int volum;
			
			// Box 용량 업데이트
			if (resultMap != null && resultMap.containsKey("battery")) {
			    int batteryCount = resultMap.get("battery");
			    volum = box.getVolume1() + batteryCount * volum1;
			    box.setVolume1(volum);
			} else if(resultMap != null && resultMap.containsKey("discharged")) {
				int batteryCount = resultMap.get("discharged");
				volum = box.getVolume2() + batteryCount * volum2;
			    box.setVolume2(volum);
			} else if(resultMap != null && resultMap.containsKey("notDischarged")) {
				int batteryCount = resultMap.get("notDischarged");
				volum = box.getVolume3() + batteryCount * volum3;
			    box.setVolume3(volum);
			}
			
			boxRepository.save(box);
			
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
		alarm(boxId);
		return point;
	}

	// 익명 사용자 수거함 이용
	public String boxUse(CloseBoxResponseDTO dto) {
		alarm(dto.getBoxId());
		Map<String, Integer> resultMap = dto.getResult();
		int volum;
		
		Box box = findBoxById(dto.getBoxId());
	
		
		// Box 용량 업데이트
		if (resultMap != null && resultMap.containsKey("battery")) {
		    int batteryCount = resultMap.get("battery");
		    volum = box.getVolume1() + batteryCount * volum1;
		    box.setVolume1(volum);
		} else if(resultMap != null && resultMap.containsKey("discharged")) {
			int batteryCount = resultMap.get("discharged");
			volum = box.getVolume2() + batteryCount * volum2;
		    box.setVolume2(volum);
		} else if(resultMap != null && resultMap.containsKey("notDischarged")) {
			int batteryCount = resultMap.get("notDischarged");
			volum = box.getVolume3() + batteryCount * volum3;
		    box.setVolume3(volum);
		}
		
		boxRepository.save(box);
		return boxLogService.boxUse(dto, saveFile(dto.getImage()));
	}
	
	// 수거 권장 및 필요 알람
	private void alarm(int boxId) {
		Box box = findBoxById(boxId);
		int maxVolume = Math.max(box.getVolume1(),
                Math.max(box.getVolume2(), box.getVolume3()));
		if (maxVolume >= 50) {
			String alertType = (maxVolume >= 70) ? "수거 필요" : "수거 권장";
	        
	        WebClient webClient = webClientBuilder.baseUrl(webIP).build();
	        webClient.post()
	        .uri("/alerts")
	        .contentType(MediaType.APPLICATION_JSON)
	        .bodyValue(Map.of(
	            "boxId", box.getId(),
	            "alertType", alertType
	        ))
	        .retrieve()
	        .bodyToMono(Void.class)
	        .doOnError(err -> 
            System.err.printf(
                    "알림 전송 실패: boxId=%d, alertType=%s, err=%s%n",
                    box.getId(), alertType, err.getMessage()
                )
            )
	        .block();  // 비동기 전송
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
				String filePath = uploadDir + fileName;

				// 파일 저장
				File imageFile = new File(filePath);
				try (OutputStream os = new FileOutputStream(imageFile)) {
					os.write(imageBytes);
				}
				return fileName;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
