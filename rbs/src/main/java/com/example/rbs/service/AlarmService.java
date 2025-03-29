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
	public void createAlarm(int boxId, String role, AlarmType alarmType) {
		Alarm alarm = new Alarm();
		alarm.setBoxId(boxId);
		alarm.setDate(new Date());
		alarm.setResolved(AlarmStatus.UNRESOLVED);
		alarm.setRole(role);
		alarm.setUserId(userService.getUserId());
		alarm.setTargetUserId(null);
		alarm.setType(alarmType);

		alarmRepository.save(alarm);
	}

	// 수거함 설치 요청
	// 새로운 알람 생성
	@Transactional
	public String installRequest(BoxDTO boxDTO) {
		try {
			// installRequest(boxDTO) name, IPAddress, Location
			int boxId = boxService.installRequest(boxDTO);
			// createAlarm(int boxId, String role, AlarmType alarmType)
			createAlarm(boxId, "ROLE_ALL", AlarmType.INSTALL_REQUEST);
			
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

	// 수거함 제거 요청
	// 새로운 알람 생성
	@Transactional
	public String removeRequest(int boxId) {
		try {
			boxService.boxStatusUpdate(boxId, InstallStatus.REMOVE_REQUEST);
			// createAlarm(int boxId, String role, AlarmType alarmType)
			createAlarm(boxId, "ROLE_ALL", AlarmType.REMOVE_REQUEST);
			
			return "Success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

	// 알람 상태 변경
	// 수거함 설치/제거 과정
	@Transactional
	public String alarmUpdate(int alarmId, AlarmType alarmType, String role, BoxDTO boxDTO) {
		try {
			Optional<Alarm> alarm = alarmRepository.findById(alarmId);
			if (alarm.isPresent()) {
				Alarm myAlarm = alarm.get();
				myAlarm.setType(alarmType);
				myAlarm.setRole(role);
				if (!alarmType.equals(AlarmType.INSTALL_COMPLETED) || !alarmType.equals(AlarmType.REMOVE_COMPLETED)) {
					myAlarm.setTargetUserId(myAlarm.getUserId());
					myAlarm.setUserId(userService.getUserId());
				}
				alarmRepository.save(myAlarm);
				
				if(alarmType.equals(AlarmType.INSTALL_COMPLETED)) {
					boxService.boxStatusUpdate(myAlarm.getBoxId(), InstallStatus.valueOf(alarmType.name()), boxDTO);
				} else {
					boxService.boxStatusUpdate(myAlarm.getBoxId(), InstallStatus.valueOf(alarmType.name()));
				}
				
				return "Success";
			} else {
				return "Fail";
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}
	
	// 알람 상태 변경
	// 수거함 수거 예약 과정
	@Transactional
	public String collectionAlarmUpdate(int alarmId, AlarmType alarmType, String role) {
		try {
			Optional<Alarm> alarm = alarmRepository.findById(alarmId);
			if (alarm.isPresent()) {
				Alarm myAlarm = alarm.get();
				myAlarm.setType(alarmType);
				myAlarm.setRole(role);
				if (!alarmType.equals(AlarmType.COLLECTION_COMPLETED)) {
					myAlarm.setTargetUserId(myAlarm.getUserId());
					myAlarm.setUserId(userService.getUserId());
				}
				
				if(alarmType.equals(AlarmType.COLLECTION_CONFIRMED)) {
					boxService.collectionConFirmed(myAlarm.getBoxId());
				}
				alarmRepository.save(myAlarm);
				
				return "Success";
			} else {
				return "Fail";
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

}
