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
		if(box.isPresent()) {
			return box.get();
		} else {
			return null;
		}
	}

	// 수거함 차단 및 해제
	public String blockBox(int id) {
		Optional<Box> box = boxRepository.findById(id);
		if(box.isPresent()) {
			Box myBox =  box.get();
			if(myBox.getUsed() == -1) { // 누군가 사용 중
				return "-1";
			} else if(myBox.getUsed() == 0) { // 차단하기
				myBox.setUsed(1);
				boxRepository.save(myBox);
				return "차단 성공";
			} else if(myBox.getUsed() == 1) { // 차단 해제하기
				myBox.setUsed(0);
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
		if(box.isPresent()) {
			Box myBox =  box.get();
			myBox.setUsed(1);
			boxRepository.save(myBox);
			return "Success";
		} else {
			return "Fail";
		}
	}

	// 수거함 제어
	public String boxControll(String controll, String role, int id) {
		Optional<Box> box = boxRepository.findById(id);
		if(box.isPresent()) {
			WebClient webClient = webClientBuilder.baseUrl("http://" + box.get().getIPAddress()).build();
			
			String uri;
			if(role.equals("user")) {
				uri = "user" + controll; // user open or close
			} else if(role.equals("admin")) {
				uri = "admin" + controll; // admin open or close
			} else {
				return "권한이 존재하지 않습니다.";
			}
			
			Mono<String> responseMono = webClient.get().uri(uri)
		            .retrieve().bodyToMono(String.class).timeout(Duration.ofSeconds(60)) // 시간 초과 처리
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
