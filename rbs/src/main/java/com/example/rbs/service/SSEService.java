package com.example.rbs.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.rbs.entity.Alarm;
import com.example.rbs.model.UserEmitter;

@Service
public class SSEService {
	
	private final UserService userService;
	
	public SSEService(UserService userService) {
		this.userService = userService;
	}
	
	private final Map<String, UserEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter subscribe() {
		
		String userId = userService.getUserId();
		String role = userService.getUserRole();
		
		if (emitters.containsKey(userId)) {
	        emitters.get(userId).getEmitter().complete();
	        emitters.remove(userId);
	    }
		
		SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, new UserEmitter(emitter, role));
        
        
		return emitter;
	}
	
	public void sendAlarmToUser (Alarm alarm) {
		
		String alarmUserId = alarm.getUserId();
        String alarmTargetUserId = alarm.getTargetUserId();
        String alarmRole = alarm.getRole();
		
        emitters.forEach((userId, userEmitter) -> {
            String userRole = userEmitter.getRole();
            
            // user_id 또는 target_user_id가 현재 사용자 ID와 일치하는 경우 또는 역할(role)이 일치하는 경우
            if ((alarmUserId != null && alarmUserId.equals(userId)) ||
                (alarmTargetUserId != null && alarmTargetUserId.equals(userId)) ||
                (alarmRole != null && alarmRole.equals(userRole))) {
                
                try {
                    System.out.println("Sending alarm to: " + userId);
                    userEmitter.getEmitter().send(SseEmitter.event().name("alarm").data(alarm));
                } catch (IOException e) {
                    emitters.remove(userId);
                }
            }
        });

    }

}
