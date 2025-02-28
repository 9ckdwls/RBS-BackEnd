package com.example.rbs.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;
import com.example.rbs.entity.Alarm.AlarmType;
import com.example.rbs.entity.Box.InstallStatus;
import com.example.rbs.repository.AlarmRepository;
import jakarta.transaction.Transactional;

@Service
public class AlarmService {

	private AlarmRepository alarmRepository;
	private BoxService boxService;
	private UserService userService;

	public AlarmService(AlarmRepository alarmRepository, BoxService boxService, UserService userService) {
		this.alarmRepository = alarmRepository;
		this.boxService = boxService;
		this.userService = userService;
	}

	// 미해결된 알람 가져오기
	public List<Alarm> unResolved() {
		return alarmRepository.findRelevantAlarms(userService.getUserRole(), userService.getUserRole());
	}

	// 새로운 알람 생성
	// parentAlarmId가 -1이면 null
	public void createAlarm(int boxId, String role, AlarmType alarmType, int parentAlarmId) {
		Alarm alarm = new Alarm();
		alarm.setBoxId(boxId);
		alarm.setDate(new Date());
		if (parentAlarmId != -1) {
			alarm.setParentAlarmId(parentAlarmId);
		}
		alarm.setResolved(AlarmStatus.UNRESOLVED);
		alarm.setRole(role);
		alarm.setType(alarmType);
		alarm.setUserId(userService.getUserId());

		alarmRepository.save(alarm);
	}

	// 알람 상태 변경
	// return 알람의 boxId
	public int alarmUpdate(int alarmId, AlarmStatus alarmStatus) {
		Optional<Alarm> alarm = alarmRepository.findById(alarmId);
		if (alarm.isPresent()) {
			Alarm myAlarm = alarm.get();
			myAlarm.setResolved(alarmStatus);
			return myAlarm.getBoxId();
		} else {
			throw new RuntimeException("해당 알람이 존재하지 않습니다.");
		}
	}

	// 수거함 설치 요청
	// 새로운 알람 생성
	@Transactional
	public String installRequest(BoxDTO boxDTO) {
		try {
			// createAlarm(int boxId, String role, AlarmType alarmType, int parentAlarmId)
			int boxId = boxService.installRequest(boxDTO);
			createAlarm(boxId, "ROLE_EMPLOYEE", AlarmType.INSTALL_REQUEST, -1);
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

	// 수거함 요청 처리
	// 기존 알람 상태 변경
	// 수거함 상태 변경
	// 새로운 알람 생성
	@Transactional
	public String alarmRequest(int alarmId, AlarmStatus alarmStatus, InstallStatus installStatus, String role,
			AlarmType alarmType) {
		try {
			// createAlarm(int boxId, String role, AlarmType alarmType, int parentAlarmId)
			// alarmUpdate(int alarmId, AlarmStatus alarmStatus)
			// boxStatusUpdate(int id, InstallStatus installStatus)
			// UNRESOLVED 추가처리 필요
			int boxId = alarmUpdate(alarmId, alarmStatus);
			boxService.boxStatusUpdate(boxId, installStatus);
			createAlarm(boxId, role, alarmType, alarmId);
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

	// 수거함 요청 처리
	// 기존 알람 상태 변경
	// 수거함 상태 및 위치, 사진 업데이트
	// 새로운 알람 생성
	@Transactional
	public String alarmRequest(int alarmId, AlarmStatus alarmStatus, InstallStatus installStatus, String role,
			AlarmType alarmType, BoxDTO boxDTO) {
		try {
			// createAlarm(int boxId, String role, AlarmType alarmType, int parentAlarmId)
			// alarmUpdate(int alarmId, AlarmStatus alarmStatus)
			// boxStatusUpdate(int id, InstallStatus installStatus, BoxDTO boxDTO)
			int boxId = alarmUpdate(alarmId, alarmStatus);
			boxService.boxStatusUpdate(boxId, installStatus, boxDTO);
			createAlarm(boxId, role, alarmType, alarmId);
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}
}
