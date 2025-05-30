package com.example.rbs.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.rbs.dto.BoxDTO;
import com.example.rbs.dto.CollectionDTO;
import com.example.rbs.dto.FireDto;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmType;
import com.example.rbs.service.AlarmService;

@RestController
public class AlarmController {

	private AlarmService alarmService;

	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	// 미해결된 알람 가져오기
	@GetMapping("alarm/unResolved")
	public List<Alarm> unResolved() {
		return alarmService.unResolved();
	}

	// 관리자가 볼 알람
	@GetMapping("admin/alarm/unResolved")
	public List<Alarm> adminAlarm() {
		return alarmService.adminAlarm();
	}

	// 수거함 설치 요청
	// BoxDTO: name, IPAddress, longitude(경도), latitude(위도)
	// 관리자용
	@PostMapping("admin/installRequest")
	public String installRequest(@RequestBody BoxDTO boxDTO) {
		System.out.println("설치요청 후 최초 IP주소:" + boxDTO.getIPAddress());
		return alarmService.installRequest(boxDTO);
	}

	// 수거함 설치 진행
	// 수거자용
	@PatchMapping("employee/installInProgress/{alarmId}")
	public String installInProgress(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmUpdate(alarmId, AlarmType.INSTALL_IN_PROGRESS, "ROLE_ADMIN", null, null);
	}

	// 수거함 설치 완료
	// BoxDTO: longitude(경도), latitude(위도), file(사진)
	// 수거자용
	@PatchMapping("employee/installCompleted/{alarmId}")
	public String installCompleted(@PathVariable(value = "alarmId") int alarmId, @ModelAttribute BoxDTO boxDTO) {
		return alarmService.alarmUpdate(alarmId, AlarmType.INSTALL_COMPLETED, "ROLE_ADMIN", boxDTO, boxDTO.getFile());
	}

	// 수거함 설치 확정
	// 관리자용
	@PatchMapping("admin/installConFiremed/{alarmId}")
	public String installConFiremed(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmUpdate(alarmId, AlarmType.INSTALL_CONFIRMED, "null", null, null);
	}

	// 수거함 설치 확정 수거자 확인 완료
	// 수거자용
	@PatchMapping("employee/installEnd/{alarmId}")
	public String installEnd(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmResolved(alarmId);
	}

	// 수거함 제거 요청
	// 관리자용
	@PatchMapping("admin/removeRequest/{boxId}")
	public String removeRequest(@PathVariable(value = "boxId") int boxId) {
		return alarmService.removeRequest(boxId);
	}

	// 수거함 제거 진행
	// 수거자용
	@PatchMapping("employee/removeInProgress/{alarmId}")
	public String removeInProgress(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmUpdate(alarmId, AlarmType.REMOVE_IN_PROGRESS, "ROLE_ADMIN", null, null);
	}

	// 수거함 제거 완료
	// 수거자용
	@PatchMapping("employee/removeCompleted/{alarmId}")
	public String removeCompleted(@PathVariable(value = "alarmId") int alarmId, @RequestParam("file") MultipartFile file) {
		return alarmService.alarmUpdate(alarmId, AlarmType.REMOVE_COMPLETED, "ROLE_ADMIN", null, file);
	}

	// 수거함 제거 확정
	// 수거자용
	@PatchMapping("admin/removeConFiremed/{alarmId}")
	public String removeConFiremed(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmUpdate(alarmId, AlarmType.REMOVE_CONFIRMED, "null", null, null);
	}

	// 수거함 제거 확정 수거자 확인 완료
	// 수거자용
	@PatchMapping("employee/removeEnd/{alarmId}")
	public String removeEnd(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmResolved(alarmId);
	}

	// 수거 진행
	// 수거자용
	@PatchMapping("employee/collectionInProgress/{alarmId}")
	public String collectionInProgress(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.collectionAlarmUpdate(alarmId, AlarmType.COLLECTION_IN_PROGRESS, "ROLE_ADMIN", null);
	}

	// 수거함 문열기
	// 수거자용
	@GetMapping("employee/boxOpen/{alarmId}/{boxId}")
	public String boxOpen(@PathVariable(value = "alarmId") int alarmId, @PathVariable(value = "boxId") int boxId) {
		return alarmService.boxOpen(alarmId, boxId);
	}

	// 수거 완료
	// 수거자용
	@PatchMapping("employee/collectionCompleted/{alarmId}")
	public String collectionCompleted(@PathVariable(value = "alarmId") int alarmId, @RequestParam("file") MultipartFile file) {
		return alarmService.collectionAlarmUpdate(alarmId, AlarmType.COLLECTION_COMPLETED, "ROLE_ADMIN", file);
	}

	// 수거 확정
	// 관리자용
	@PatchMapping("admin/collectionConFirmed/{alarmId}")
	public String collectionConFirmed(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.collectionAlarmUpdate(alarmId, AlarmType.COLLECTION_CONFIRMED, null, null);
	}

	// 수거 확정 수거자 확인 완료
	// 수거자용
	@PatchMapping("employee/collectioneEnd/{alarmId}")
	public String collectioneEnd(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmResolved(alarmId);
	}

	// 화재 처리 진행
	// 수거자용
	@PatchMapping("employee/fireInProgress/{alarmId}")
	public String fireInProgress(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.fireAlarmUpdate(alarmId, AlarmType.FIRE_IN_PROGRESS, "ROLE_ADMIN", null);
	}

	// 화재 처리 완료
	// 수거자용
	@PatchMapping("employee/fireCompleted/{alarmId}")
	public String fireCompleted(@PathVariable(value = "alarmId") int alarmId, @RequestParam("file") MultipartFile file) {
		return alarmService.fireAlarmUpdate(alarmId, AlarmType.FIRE_COMPLETED, "ROLE_ADMIN", file);
	}

	// 화재 처리 확정
	// 관리자용
	@PatchMapping("admin/fireConFirmed/{alarmId}")
	public String fireConFirmed(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.fireAlarmUpdate(alarmId, AlarmType.FIRE_CONFIRMED, null, null);
	}

	// 화재 처리 확정 수거자 확인 완료
	// 수거자용
	@PatchMapping("employee/fireEnd/{alarmId}")
	public String fireEnd(@PathVariable(value = "alarmId") int alarmId) {
		return alarmService.alarmResolved(alarmId);
	}

	// 화재 로그 보기
	// 관리자용
	@GetMapping("admin/fireLog")
	public List<Alarm> fireLog() {
		return alarmService.fireLog();
	}

	// 화재 발생
	@PostMapping("/fire")
	public String fire(@RequestBody FireDto fireDto) {
		System.out.println(fireDto);
		return alarmService.fire(fireDto);
	}
	
	// 수거 알람 발생
	@PostMapping("/alerts")
	public String collection(@RequestBody CollectionDTO dto) {
		System.out.println("웹 서버측");
		System.out.println(dto);
		return alarmService.collection(dto);
	}
}
