package com.example.rbs.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SSEService {
	
	private final UserService userService;
	
	public SSEService(UserService userService) {
		this.userService = userService;
	}
	
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter subscribe() {
		
		String userId = userService.getUserId();
		
		if (emitters.containsKey(userId)) {
	        emitters.get(userId).complete();
	        emitters.remove(userId);
	    }
		
		SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        
        
		return emitter;
	}
	
	public void sendAlarmToUser(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
            	System.out.println(userService.getUserId());
            	 Map<String, String> data = new HashMap<>();
                 data.put("message", message);
                 emitter.send(SseEmitter.event().name("alarm").data(data));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

}
