package com.example.rbs.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.rbs.dto.BoxDTO;
import com.example.rbs.dto.CloseBoxResponseDTO;
import com.example.rbs.dto.IOTResponseDTO;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.Box.FireStatus;
import com.example.rbs.entity.Box.InstallStatus;
import com.example.rbs.entity.Box.UsageStatus;
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

	// 수거함 이름으로 검색
	public Box findBoxByName(String name) {
		Optional<Box> optionalBox = boxRepository.findByName(name);

		if (optionalBox.isPresent()) {
			return optionalBox.get();
		} else {
			return null;
		}
	}

	// 수거함 차단 및 해제
	public String blockBox(int id) {
		Optional<Box> box = boxRepository.findById(id);
		if (box.isPresent()) {
			Box myBox = box.get();
			if (myBox.getUsageStatus() == UsageStatus.USED) { // 누군가 사용 중
				return "-1";
			} else if (myBox.getUsageStatus() == UsageStatus.AVAILABLE) { // 차단하기
				myBox.setUsageStatus(UsageStatus.BLOCKED);
				boxRepository.save(myBox);
				return "차단 성공";
			} else if (myBox.getUsageStatus() == UsageStatus.BLOCKED) { // 차단 해제하기
				myBox.setUsageStatus(UsageStatus.AVAILABLE);
				boxRepository.save(myBox);
				return "차단 해제 성공";
			} else {
				return "Fail";
			}
		} else {
			return "Fail";
		}
	}

	// 사용 중인 수거함 강제 차단
	public String superBlockBox(int id) {
		Optional<Box> box = boxRepository.findById(id);
		if (box.isPresent()) {
			Box myBox = box.get();
			myBox.setUsageStatus(UsageStatus.BLOCKED);
			boxRepository.save(myBox);
			return "Success";
		} else {
			return "Fail";
		}
	}

	// 수거함 제어
	public Object boxControll(String controll, int id, int number) {
		Box box = findById(id);
		String uri;
		String role = userService.getUserRole();

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
			System.out.println("IOT 문열기 후 결과는?");
			System.out.println(response);

			return response;
		} else if (controll.equals("boxClose")) {
			// 문 닫기 요청 → 사진 및 수거 로그도 포함된 응답 받기
			CloseBoxResponseDTO response = webClient
					.post()
					.uri(uri)
					.retrieve()
					.bodyToMono(CloseBoxResponseDTO.class)
					.timeout(Duration.ofSeconds(60))
					.onErrorResume(TimeoutException.class,
							t -> Mono.just(new CloseBoxResponseDTO("Fail")))
					.onErrorResume(ConnectException.class,
							t -> Mono.just(new CloseBoxResponseDTO("Fail")))
					.block();
			
			System.out.println("IOT 문닫기 후 결과는?");
			System.out.println(response);
			return response;
		} else {
			return "Fail";
		}
	}

	// boxId로 Box 찾기
	// 공통된 로직 처리
	public Box findById(int boxId) {
		Optional<Box> box = boxRepository.findById(boxId);
		if (box.isPresent()) {
			return box.get();
		} else {
			throw new RuntimeException("해당 수거함이 존재하지 않습니다.");
		}
	}

	// 수거함 설치 요청
	public int installRequest(BoxDTO boxDTO) {
		Box box = new Box();
		box.setName(boxDTO.getName());
		box.setIPAddress(boxDTO.getIPAddress());
		box.setLocation(boxDTO.toPoint());
		box.setFireStatus1(FireStatus.UNFIRE);
		box.setFireStatus2(FireStatus.UNFIRE);
		box.setFireStatus3(FireStatus.UNFIRE);
		box.setInstallStatus(InstallStatus.INSTALL_REQUEST);
		box.setStore1(0);
		box.setStore2(0);
		box.setStore3(0);
		box.setUsageStatus(UsageStatus.AVAILABLE);
		box.setVolume1(0);
		box.setVolume2(0);
		box.setVolume3(0);
		boxRepository.save(box);

		return box.getId();
	}

	// 수거함 상태 최신화
	public void boxStatusUpdate(int id, InstallStatus installStatus) {
		Box box = findById(id);
		box.setInstallStatus(installStatus);
		boxRepository.save(box);
	}

	// 수거함 상태 변경
	public void boxFireStatusUpdate(int id) {
		Box box = findById(id);
		box.setFireStatus1(FireStatus.UNFIRE);
		box.setFireStatus2(FireStatus.UNFIRE);
		box.setFireStatus3(FireStatus.UNFIRE);
		boxRepository.save(box);
	}

	// 수거함 설치 완료 시 위치 최신화
	public void boxStatusUpdate(int id, InstallStatus installStatus, BoxDTO boxDTO) {
		Box box = findById(id);
		box.setInstallStatus(installStatus);
		box.setLocation(boxDTO.toPoint());
		boxRepository.save(box);
	}

	// 사진 파일 저장
	public void savefile(int boxId, String saveFile) {
		Box box = findById(boxId);
		box.setFile(saveFile);

		boxRepository.save(box);
	}

	// 수거 완료
	// 수거함 상태 업데이트
	public void collectionCompleted(int boxId) {
		Box box = findById(boxId);
		box.setVolume1(0);
		box.setVolume2(0);
		box.setVolume3(0);
		boxRepository.save(box);
	}
}
