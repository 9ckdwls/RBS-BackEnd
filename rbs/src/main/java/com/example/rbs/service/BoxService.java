package com.example.rbs.service;

import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.rbs.dto.BoxDTO;
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

	public BoxService(BoxRepository boxRepository, WebClient.Builder webClientBuilder) {
		this.boxRepository = boxRepository;
		this.webClientBuilder = webClientBuilder;
	}

	// 모든 수거함 조회
	public List<Box> findAllBox() {
		return boxRepository.findAll();
	}

	// 수거함 이름으로 검색
	public Box findBoxByName(String name) {
		Optional<Box> box = boxRepository.findByName(name);
		if (box.isPresent()) {
			return box.get();
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
	public String boxControll(String controll, String role, int id, int number) {
		Optional<Box> box = boxRepository.findById(id);
		if (box.isPresent()) {
			WebClient webClient = webClientBuilder.baseUrl("http://" + box.get().getIPAddress()).build();

			String uri;
			if (role.equals("user")) {
				uri = "user" + controll + number; // user open or close
			} else if (role.equals("employee")) {
				uri = "employee" + controll + number; // admin open or close
			} else {
				return "권한이 존재하지 않습니다.";
			}

			Mono<String> responseMono = webClient.get().uri(uri).retrieve().bodyToMono(String.class)
					.timeout(Duration.ofSeconds(60)) // 시간 초과 처리
					.onErrorResume(e -> {
						if (e instanceof java.net.SocketTimeoutException) {
							return Mono.just("시간 초과로 인해 요청을 처리할 수 없습니다.");
						} else if (e instanceof ConnectException) {
							// 네트워크 연결 문제 처리
							return Mono.just("네트워크 연결 실패: IoT 장비에 연결할 수 없습니다.");
						} else {
							return Mono.just("알 수 없는 오류");
						}
					});
			try {
				return responseMono.toFuture().join();
			} catch (CompletionException e) {
				return e.toString();
			}
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

	// 수거 완료
	public void collectionConFirmed(int boxId) {
		Box box = findById(boxId);
		box.setStore1(0);
		box.setStore2(0);
		box.setStore3(0);
		boxRepository.save(box);
	}
}
