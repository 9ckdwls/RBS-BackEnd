package com.example.rbs.service;

import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
		Optional<Box> box =  boxRepository.findById(id);
		if(box.isPresent()) {
			return box.get();
		} else {
			return null;
		}
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

	// 수거함 제어
	public String boxControll(String controll, int id) {
		Optional<Box> box = boxRepository.findById(id);
		if (box.isPresent()) {
			WebClient webClient = webClientBuilder.baseUrl("http://" + box.get().getIPAddress()).build();
			
			String role = userService.getRole();
			
			String uri;
			if (role.equals("user")) {
				uri = "user" + controll; // user open or close
			} else if (role.equals("admin")) {
				uri = "admin" + controll; // admin open or close
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
}
