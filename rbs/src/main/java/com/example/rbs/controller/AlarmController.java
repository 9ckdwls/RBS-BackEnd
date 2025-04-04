package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;
import com.example.rbs.entity.Alarm.AlarmType;
import com.example.rbs.entity.Box.InstallStatus;
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
	// BoxDTO(name, IPAddress, Location)
	// 관리자용
	@PostMapping("admin/installRequest")
	public String installRequest(@RequestBody BoxDTO boxDTO) {
		return alarmService.installRequest(boxDTO);
	}

	// 수거함 설치 진행
	// 수거자용
	@PatchMapping("employee/installInProgress/{id}")
	public String installInProgress(@PathVariable(value = "id") int id) {
		return alarmService.alarmUpdate(id, AlarmType.INSTALL_IN_PROGRESS, "ROLE_ADMIN", null);
	}

	// 수거함 설치 완료
	// BoxDTO(Location)
	// 수거자용
	@PatchMapping("employee/installCompleted/{id}")
	public String installCompleted(@PathVariable(value = "id") int id, @RequestBody BoxDTO boxDTO) {
		return alarmService.alarmUpdate(id, AlarmType.INSTALL_COMPLETED, "ROLE_ADMIN", boxDTO);
	}

	// 수거함 설치 확정
	// 관리자용
	@PatchMapping("admin/installConFiremed/{id}")
	public String installConFiremed(@PathVariable(value = "id") int id) {
		return alarmService.alarmUpdate(id, AlarmType.INSTALL_CONFIRMED, "null", null);
	}

	// 수거함 제거 요청
	// 관리자용
	@PatchMapping("admin/removeRequest/{boxId}")
	public String removeRequest(@PathVariable(value = "boxId") int boxId) {
		return alarmService.removeRequest(boxId);
	}

	// 수거함 제거 진행
	// 수거자용
	@PatchMapping("employee/removeInProgress/{id}")
	public String removeInProgress(@PathVariable(value = "id") int id) {
		return alarmService.alarmUpdate(id, AlarmType.REMOVE_IN_PROGRESS, "ROLE_ADMIN", null);
	}

	// 수거함 제거 완료
	// 수거자용
	@PatchMapping("employee/removeCompleted/{id}")
	public String removeCompleted(@PathVariable(value = "id") int id) {
		return alarmService.alarmUpdate(id, AlarmType.REMOVE_COMPLETED, "ROLE_ADMIN", null);
	}

	// 수거함 제거 확정
	// 수거자용
	@PatchMapping("admin/removeConFiremed/{id}")
	public String removeConFiremed(@PathVariable(value = "id") int id) {
		return alarmService.alarmUpdate(id, AlarmType.REMOVE_CONFIRMED, "null", null);
	}

	// 수거 진행
	// 수거자용
	@PatchMapping("employee/collectionInProgress/{id}")
	public String collectionInProgress(@PathVariable(value = "id") int id) {
		return alarmService.collectionAlarmUpdate(id, AlarmType.COLLECTION_IN_PROGRESS, "ROLE_ADMIN");
	}
	
	// 수거 완료
	// 관리자용
	@PatchMapping("employee/collectionCompleted/{id}")
	public String collectionCompleted(@PathVariable(value = "id") int id) {
		return alarmService.collectionAlarmUpdate(id, AlarmType.COLLECTION_COMPLETED, "ROLE_ADMIN");
	}
	
	// 수거 확정
	// 수거자용
	@PatchMapping("employee/collectionConFirmed/{id}")
	public String collectionConFirmed(@PathVariable(value = "id") int id) {
		return alarmService.collectionAlarmUpdate(id, AlarmType.COLLECTION_CONFIRMED, null);
	}

}
