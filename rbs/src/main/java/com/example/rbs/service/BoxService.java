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
import com.example.rbs.dto.CloseBoxUserResponseDTO;
import com.example.rbs.dto.IOTResponseDTO;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.Box.InstallStatus;
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

	// 설치된 수거함 조회
	public List<Box> findAllBox() {
		List<InstallStatus> statuses = List.of(InstallStatus.INSTALL_CONFIRMED, InstallStatus.REMOVE_REQUEST,
				InstallStatus.REMOVE_IN_PROGRESS, InstallStatus.REMOVE_COMPLETED);
		return boxRepository.findByInstallStatusIn(statuses);
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
	public int boxControll(String controll, int boxId, int number) {
		Box box = findBoxById(boxId);
		String uri;
		String role = userService.getRole();

		uri = controll + number;

		WebClient webClient = webClientBuilder.baseUrl("http://" + box.getIPAddress()).build();

		if (controll.equals("boxOpen")) {
			if (box.getStore1() == 1 || box.getStore2() == 1 || box.getStore3() == 1 || box.getStore4() == 1) {
				return 0;
			}
			if (number == 0) {
				box.setStore1(1);
			} else if (number == 1) {
				box.setStore2(1);
			} else if (number == 2) {
				box.setStore3(1);
			}

			boxRepository.save(box);

			IOTResponseDTO response = webClient.post().uri(uri).retrieve().bodyToMono(IOTResponseDTO.class)
					.timeout(Duration.ofSeconds(60))
					.onErrorResume(TimeoutException.class, t -> Mono.just(new IOTResponseDTO("Fail")))
					.onErrorResume(ConnectException.class, t -> Mono.just(new IOTResponseDTO("Fail"))).block();

			return 1;
		} else if (controll.equals("boxClose")) {
			// 문 닫기 요청 → 사진 및 수거 로그도 포함된 응답 받기
			System.out.println("문닫기 요청");

			if (number == 0) {
				if (box.getStore1() == 0) {
					return 0;
				}
				box.setStore1(0);
			} else if (number == 1) {
				if (box.getStore1() == 0) {
					return 0;
				}
				box.setStore2(0);
			} else if (number == 2) {
				if (box.getStore1() == 0) {
					return 0;
				}
				box.setStore3(0);
			}

			String userId = userService.getId();
			Map<String, Object> payload = Collections.singletonMap("userId", userId);
			CloseBoxResponseDTO response = webClient.post().uri(uri).bodyValue(payload).retrieve()
					.bodyToMono(CloseBoxResponseDTO.class).timeout(Duration.ofSeconds(60))
					.onErrorReturn(new CloseBoxResponseDTO("Fail")).block();
			boxRepository.save(box);

			return 1;
		} else {
			return 0;
		}
	}

	// 수거함 사용 끝
	public int boxEnd(int boxId) {
		int point = boxLogService.boxEnd(boxId);
		userService.updatePoint(point);
		alarm(boxId);
		return point;
	}

	// 익명 사용자 수거함 사용
	public void userBoxOpen(CloseBoxUserResponseDTO dto) {
		Box box =findBoxById(dto.getBoxId());
		if(dto.getNum()==0) {
			box.setStore1(1);
		} else if(dto.getNum()==0) {
			box.setStore2(1);
		} else if(dto.getNum()==0) {
			box.setStore3(1);
		}
		boxRepository.save(box);
	}

	// 익명 사용자 수거함 닫기
	public String boxUse(CloseBoxResponseDTO dto) {
		Map<String, Integer> resultMap = dto.getResult();
		int volum;

		if (resultMap == null) {
			return "Fail";
		}

		Box box = findBoxById(dto.getBoxId());

		// Box 용량 업데이트
		if (resultMap != null && resultMap.containsKey("battery")) {
			int batteryCount = resultMap.get("battery");
			volum = box.getVolume1() + batteryCount * volum1;
			box.setVolume1(volum);
		} else if (resultMap != null && resultMap.containsKey("Electronic devices")) {
			int batteryCount = resultMap.get("Electronic devices");
			volum = box.getVolume3() + batteryCount * volum3;
			box.setVolume3(volum);
		}

		boxRepository.save(box);
		alarm(dto.getBoxId());
		return boxLogService.boxUse(dto, saveFile(dto.getImage()));
	}

	// 사용자 수거함 닫기
	public void boxUseUser(CloseBoxUserResponseDTO dto) {

		Box box = findBoxById(dto.getBoxId());

		Map<String, Integer> resultMap = dto.getResult();

		int volum;

		if (resultMap == null) {
			return;
		}

		if (resultMap.isEmpty()) {
			return;
		}
		// Box 용량 업데이트
		if (resultMap != null && dto.getNum() == 0) {
			int batteryCount = resultMap.get("battery");
			volum = box.getVolume1() + batteryCount * volum1;
			box.setVolume1(volum);
		} else if (resultMap != null && dto.getNum() == 1) {
			int batteryCount = resultMap.get("Electronic devices");
			volum = box.getVolume2() + batteryCount * volum2;
			box.setVolume2(volum);
		} else if (resultMap != null && dto.getNum() == 2) {
			int batteryCount = resultMap.get("Electronic devices");
			volum = box.getVolume3() + batteryCount * volum3;
			box.setVolume3(volum);
		}

		boxRepository.save(box);

		// 로그 작성 및 사진파일 저장
		boxLogService.logUpdate(box.getId(), dto.getResult(), saveFile(dto.getImage()), dto.getNum());
	}

	// 수거 권장 및 필요 알람
	private void alarm(int boxId) {
		Box box = findBoxById(boxId);
		int maxVolume = Math.max(box.getVolume1(), Math.max(box.getVolume2(), box.getVolume3()));
		if (maxVolume >= 50) {
			String alertType = (maxVolume >= 80) ? "수거 필요" : "수거 권장";

			WebClient webClient = webClientBuilder.baseUrl(webIP).build();
			webClient.post().uri("/alerts").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Map.of("boxId", box.getId(), "alertType", alertType)).retrieve().bodyToMono(Void.class)
					.doOnError(err -> System.err.printf("알림 전송 실패: boxId=%d, alertType=%s, err=%s%n", box.getId(),
							alertType, err.getMessage()))
					.block(); // 비동기 전송
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
