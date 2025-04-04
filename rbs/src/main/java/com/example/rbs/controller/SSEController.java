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
	
}
