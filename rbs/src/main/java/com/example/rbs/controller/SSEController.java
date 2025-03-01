package com.example.rbs.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.rbs.service.SSEService;

@RestController
public class SSEController {
	
	private final SSEService sseService;
	
	public SSEController(SSEService sseService) {
		this.sseService = sseService;
	}
	
	@GetMapping("/SSEsubscribe")
	public SseEmitter subscribe() {
		System.out.println("요청옴");
		return sseService.subscribe();
	}
	
	@GetMapping("/test")
	public String test() {
		System.out.println("요청옴2");
		sseService.sendAlarmToUser("admin1", "테스트 데이터");
		return "ok";
	}
	
	
}
