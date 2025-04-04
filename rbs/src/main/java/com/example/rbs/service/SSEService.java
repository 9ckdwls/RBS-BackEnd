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
	
	// ConcurrentHashMap는 멀티스레드에서 안전하게 사용가능한 해시맵
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
	
	// 알람 전송
	public void sendAlarmToUser (Alarm alarm) {
		
        String alarmTargetUserId = alarm.getTargetUserId();
        String alarmRole = alarm.getRole();
		
        emitters.forEach((userId, userEmitter) -> {
            String userRole = userEmitter.getRole();
            
            // 해당 targetUserId 이거나 해당 role 이라면
            // ROLE_ALL이면 수거자와 관리자 모두에게
            if (userId.equals(alarmTargetUserId) || userRole.equals(alarmRole) || alarmRole.equals("ROLE_ALL")) {
                try {
                    System.out.println("알람 전송");
                    userEmitter.getEmitter().send(SseEmitter.event().name("alarm").data(alarm));
                } catch (IOException e) {
                    emitters.remove(userId);
                }
            }
        });

    }

}
