package com.example.rbs.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ServerStatusService {

	private final DataSource dataSource;
	private final WebClient.Builder webClientBuilder;

	public ServerStatusService(DataSource dataSource, WebClient.Builder webClientBuilder) {
		this.dataSource = dataSource;
		this.webClientBuilder = webClientBuilder;
	}

	// 서버 상태 확인
	// DB, 앱 백 서버, 사용자 앱 프론트 서버, 수거자 앱 프론트 서버 상태
	public Map<String, String> serverStatus() {

		Map<String, String> status = new HashMap<>();

		// DB 상태 확인
		try (Connection conn = dataSource.getConnection()) {
			if (!conn.isClosed()) {
				status.put("database", "UP");
			} else {
				status.put("database", "DOWN");
			}
		} catch (SQLException e) {
			status.put("database", "DOWN");
		}

		// 앱 백 서버 상태 확인
		WebClient webClient = webClientBuilder.build();

		try {
			// 응답을 동기적으로 받아서 처리
			String response = webClient.get().uri("http://localhost:8081/server-status").retrieve().bodyToMono(String.class)
					.timeout(Duration.ofSeconds(5)).block(); // 응답이 올 때까지 대기

			status.put("appServer", "UP"); // 정상 응답이면 UP
		} catch (Exception e) {
			status.put("appServer", "DOWN"); // 오류 발생 시 DOWN
		}

		// Flask 서버 상태 확인
		try {
			String flaskResponse = webClient.get().uri("http://localhost:5000/server-status").retrieve()
					.bodyToMono(String.class).timeout(Duration.ofSeconds(5)).block();

			if (flaskResponse != null && flaskResponse.contains("\"status\":\"success\"")) {
				status.put("flaskServer", "UP");
			} else {
				status.put("flaskServer", "DOWN");
			}
		} catch (Exception e) {
			status.put("flaskServer", "DOWN");
		}

		return status;
	}
}
