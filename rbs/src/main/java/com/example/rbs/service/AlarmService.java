package com.example.rbs.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.AlarmType;
import com.example.rbs.entity.BoxStatus;
import com.example.rbs.repository.AlarmRepository;
import jakarta.transaction.Transactional;

@Service
public class AlarmService {

	private AlarmRepository alarmRepository;
	private BoxService boxService;
	private UserService userService;
	private AlarmCheckService alarmCheckService;

	public AlarmService(AlarmRepository alarmRepository, BoxService boxService, UserService userService, AlarmCheckService alarmCheckService) {
		this.alarmRepository = alarmRepository;
		this.boxService = boxService;
		this.userService = userService;
		this.alarmCheckService = alarmCheckService;
	}
	
	// 미해결된 알람 가져오기
	public List<Alarm> unResolved() {
		return alarmRepository.findByResolvedAndRoles(false, "ROLE_ALL", userService.getUserRole());
	}
	
	// 새로운 알람 생성
	public void createAlarm(int boxId, String role, AlarmType alarmType) {
		Alarm alarm = new Alarm();
		alarm.setBoxId(boxId);
		alarm.setDate(new Date());
		alarm.setResolved(false);
		alarm.setRole(role);
		alarm.setType(alarmType);

		alarmRepository.save(alarm);
	}
	
	// 알람 해결 완료
	// 공통된 로직 처리
	// return 알람의 boxId
	public int alarmResolve(int alarmId) {
		Optional<Alarm> alarm = alarmRepository.findById(alarmId);
		if (alarm.isPresent()) {
			Alarm myAlarm = alarm.get();
			myAlarm.setResolved(true);
			return myAlarm.getBoxId();
		} else {
			throw new RuntimeException("해당 알람이 존재하지 않습니다.");
		}
	}

	// 수거함 요청 처리
	// 공통된 로직 처리
	@Transactional
	public String alarmRequest(int id, BoxStatus boxStatus, String role, AlarmType alarmType) {
		try {
			createAlarm(boxService.boxStatusUpdate(alarmResolve(id), boxStatus), role, alarmType);
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}
	
	// 수거함 요청 처리
	// 공통된 로직 처리
	@Transactional
	public String alarmRequest(int id, BoxStatus boxStatus, String role, AlarmType alarmType, BoxDTO boxDTO) {
		try {
			createAlarm(boxService.boxStatusUpdate(alarmResolve(id), boxStatus, boxDTO), role, alarmType);
			
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}
	
	// 수거함 설치 요청
	@Transactional
	public String installRequest(BoxDTO boxDTO) {
		try {
			createAlarm(boxService.installRequest(boxDTO), "ROLE_EMPLOYEE", AlarmType.INSTALL_REQUEST);
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}
}
