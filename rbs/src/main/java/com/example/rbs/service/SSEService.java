package com.example.rbs.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmType;
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

		System.out.println("접근 중인 유저: " + userId);

		if (emitters.containsKey(userId)) {
			emitters.get(userId).getEmitter().complete();
			emitters.remove(userId);
		}

		SseEmitter emitter = new SseEmitter(0L);
		emitters.put(userId, new UserEmitter(emitter, role));
		System.out.println("구독 완료: " + userId);

		SseEmitter.SseEventBuilder initEvent = SseEmitter.event().name("ok").data("SSE 연결 성공 후 최초 응답");
		try {
			emitter.send(initEvent);
		} catch (IOException e) {
			// 실패 시 제거
			emitters.remove(userId);
		}

		return emitter;
	}

	// 알람 전송
	public void sendAlarmToUser(Alarm alarm) {
		// 설치/제거 요청 alarmTargetUserId = NULL / alarmUserId = 해당 관리자 / alarmRole = ROLE_EMPLOYEE
		// 설치/제거 진행 alarmTargetUserId = 해당 관리자 / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 설치/제거 완료 alarmTargetUserId = 해당 관리자 / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 설치/제거 확정 alarmTargetUserId = 해당 수거자 / alarmUserId = 해당 관리자 / alarmRole = NULL
		
		// 수거 요청 alarmTargetUserId = NULL / alarmUserId = NULL / alarmRole = ROLE_EMPLOYEE
		// 수거 진행 alarmTargetUserId = NULL / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 수거 완료 alarmTargetUserId = NULL / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 수거 확정 alarmTargetUserId = 해당 수거자 / alarmUserId = 해당 관리자 / alarmRole = NULL
		
		// 화재 요청 alarmTargetUserId = NULL / alarmUserId = NULL / alarmRole = ROLE_ALL
		// 화재 진행 alarmTargetUserId = NULL / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 화재 완료 alarmTargetUserId = NULL / alarmUserId = 해당 수거자 / alarmRole = ROLE_ADMIN
		// 화재 확정 alarmTargetUserId = 해당 수거자 / alarmUserId = 해당 관리자 / alarmRole = NULL
		
		// 모든 알람의 확정 단계에서 관리자에게는 전송X
		
		String alarmTargetUserId = alarm.getTargetUserId();
		String alarmUserId = alarm.getUserId();
		String alarmRole = alarm.getRole();
		AlarmType alarmType = alarm.getType();

		emitters.forEach((userId, userEmitter) -> {
			System.out.println("SSE 전송 확인 대상: " + userId);
			String userRole = userEmitter.getRole();

			// 해당 targetUserId 이거나 해당 role 이라면
			// ROLE_ALL이면 수거자와 관리자 모두에게
			if (userId.equals(alarmTargetUserId) || userId.equals(alarmUserId) || userRole.equals(alarmRole)
					|| "ROLE_ALL".equals(alarmRole)) {
				if (!(alarmType.name().endsWith("CONFIRMED") && userRole.equals("ROLE_ADMIN"))) {
					try {
						System.out.println("알람 전송 대상: " + userId);
						userEmitter.getEmitter().send(SseEmitter.event().name("alarm").data(alarm));
					} catch (IOException e) {
						System.out.println("SSE 전송중 에러!");
						emitters.remove(userId);
					}
				}
			}
		});

	}

}
