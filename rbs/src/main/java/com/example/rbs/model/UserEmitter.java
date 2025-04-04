package com.example.rbs.model;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.Getter;

@Getter
public class UserEmitter {
	
	private final SseEmitter emitter;
	private final String role;
	
	public UserEmitter(SseEmitter emitter, String role) {
        this.emitter = emitter;
        this.role = role;
    }
}
