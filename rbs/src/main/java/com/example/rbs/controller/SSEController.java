package com.example.rbs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.rbs.service.SSEService;

@RestController
public class SSEController {
	
	private final SSEService sseService;
	
	public SSEController(SSEService sseService) {
		this.sseService = sseService;
	}
	
	// 알람 구독하기
	@GetMapping("/SSEsubscribe")
	public SseEmitter subscribe() {
		System.out.println("구독 접근");
		return sseService.subscribe();
	}
	
}
