package com.example.rbs.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

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
	private SSEService sseService;

	public AlarmService(AlarmRepository alarmRepository, BoxService boxService, UserService userService,
			SSEService sseService) {
		this.alarmRepository = alarmRepository;
		this.boxService = boxService;
		this.userService = userService;
		this.sseService = sseService;
	}

	// 미해결된 알람 가져오기
	public List<Alarm> unResolved() {
		return alarmRepository.findRelevantAlarms(userService.getUserRole(), userService.getUserId());
	}

	// 관리자가 볼 알람
	public List<Alarm> adminAlarm() {
		return alarmRepository.findByResolvedAndRoleIn(AlarmStatus.UNRESOLVED,
				List.of(userService.getUserRole(), "ROLE_ALL"));
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

		sseService.sendAlarmToUser(alarm);
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
	public String alarmUpdate(int alarmId, AlarmType alarmType, String role, BoxDTO boxDTO, MultipartFile file) {
		try {
			Optional<Alarm> alarm = alarmRepository.findById(alarmId);
			if (alarm.isPresent()) {
				Alarm myAlarm = alarm.get();
				myAlarm.setType(alarmType); // 어떤 알람인지
				myAlarm.setRole(role); // 권한 설정
				
				// 설치/제거 완료는 setTargetUserId와 setUserId가 같음
				if (!alarmType.equals(AlarmType.INSTALL_COMPLETED) && !alarmType.equals(AlarmType.REMOVE_COMPLETED)) {
					myAlarm.setTargetUserId(myAlarm.getUserId());
					myAlarm.setUserId(userService.getUserId());
					
					// 수거 완료만 좌표 최신화
					if (alarmType.equals(AlarmType.INSTALL_COMPLETED)) {
						boxService.boxStatusUpdate(myAlarm.getBoxId(), InstallStatus.valueOf(alarmType.name()), boxDTO);
					} else {
						boxService.boxStatusUpdate(myAlarm.getBoxId(), InstallStatus.valueOf(alarmType.name()));
					}
					
					// 사진 파일 저장
					saveFile(file);
				}
				alarmRepository.save(myAlarm);

				// 알람 전송
				sseService.sendAlarmToUser(myAlarm);

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
	public String collectionAlarmUpdate(int alarmId, AlarmType alarmType, String role, MultipartFile file) {
		try {
			Optional<Alarm> alarm = alarmRepository.findById(alarmId);
			if (alarm.isPresent()) {
				Alarm myAlarm = alarm.get();
				myAlarm.setType(alarmType); // 알람 타입 업데이트
				myAlarm.setRole(role); // 수거 진행과 완료는 관리자에게 관리자가 최종 확인 하면 null로

				if (alarmType.equals(AlarmType.COLLECTION_CONFIRMED)) { // 수거 확정이라면
					myAlarm.setTargetUserId(myAlarm.getUserId()); // 수거자가 최종 알람 확인
					boxService.collectionConFirmed(myAlarm.getBoxId());
				}

				myAlarm.setUserId(userService.getUserId());

				alarmRepository.save(myAlarm);
				
				// 사진 파일 저장
				if(!file.isEmpty()) {
					saveFile(file);
				}

				// 알람 전송
				sseService.sendAlarmToUser(myAlarm);

				return "Success";
			} else {
				return "Fail";
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "Fail";
		}
	}

	// 화재 처리 진행
	public String fireAlarmUpdate(int alarmId, AlarmType alarmType, String role, MultipartFile file) {
		try {
			Optional<Alarm> alarm = alarmRepository.findById(alarmId);
			if (alarm.isPresent()) {
				Alarm myAlarm = alarm.get();
				myAlarm.setType(alarmType); // 알람 타입 업데이트
				myAlarm.setRole(role);

				// 화재처리 확정
				if (alarmType.equals(AlarmType.FIRE_CONFIRMED)) {
					myAlarm.setTargetUserId(myAlarm.getUserId()); // 수거자가 최종 알람 확인
					boxService.boxFireStatusUpdate(myAlarm.getBoxId());
				}

				myAlarm.setUserId(userService.getUserId());

				alarmRepository.save(myAlarm);
				
				// 사진 파일 저장
				if(!file.isEmpty()) {
					saveFile(file);
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

	// 알람 해결됨
	public String alarmResolved(int alarmId) {
		Optional<Alarm> alarm = alarmRepository.findById(alarmId);
		if (alarm.isPresent()) {
			Alarm myAlarm = alarm.get();
			myAlarm.setResolved(AlarmStatus.RESOLVED);
			alarmRepository.save(myAlarm);
			return "Success";
		} else {
			return "Fail";
		}
	}
	
	// 사진 파일 저장
	public void saveFile(MultipartFile file) {
		try {
			// 저장할 디렉토리 경로
			String uploadDir = "C:/uploads/images/";
			
			// 디렉토리가 없으면 생성
			File dir = new File(uploadDir);
			if(!dir.exists()) {
				dir.mkdirs();
				System.out.println("디렉토리 생성");
			}
			
			// 파일 저장
			File myFile = new File(uploadDir + UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
			file.transferTo(myFile);
		} catch (Exception e) {
			throw new RuntimeException("파일 저장 오류", e);
		}
	}

}
