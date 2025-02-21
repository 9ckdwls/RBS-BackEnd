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
import com.example.rbs.entity.AlarmType;
import com.example.rbs.entity.BoxStatus;
import com.example.rbs.service.AlarmService;

import jakarta.websocket.server.PathParam;

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

	// 수거함 설치 요청
	// 관리자용
	@PostMapping("admin/installRequest")
	public String installRequest(@RequestBody BoxDTO boxDTO) {
		return alarmService.installRequest(boxDTO);
	}

	// 수거함 설치 완료
	// 수거자용
	@PatchMapping("employee/installCompleted/{id}")
	public String installCompleted(@PathVariable(value = "id") int id, @RequestBody BoxDTO boxDTO) {
		return alarmService.alarmRequest(id, BoxStatus.INSTALL_COMPLETED, "ROLE_ADMIN", AlarmType.INSTALL_COMPLETED, boxDTO);
	}

	// 수거함 설치 확정
	// 관리자용
	@PatchMapping("admin/installConFiremed/{id}")
	public String installConFiremed(@PathVariable(value = "id") int id) {
		return alarmService.alarmRequest(id, BoxStatus.INSTALL_CONFIRMED, "ROLE_EMPLOYEE", AlarmType.INSTALL_CONFIRMED);
	}
	
	// 수거함 제거 요청
	// 관리자용
	@PatchMapping("admin/removeRequest/{id}")
	public String removeRequest(@PathVariable(value = "id") int id) {
		return alarmService.alarmRequest(id, BoxStatus.REMOVE_REQUEST, "ROLE_EMPLOYEE", AlarmType.REMOVE_REQUEST);
	}
	
	// 수거함 제거 완료
	// 수거자용
	@PatchMapping("employee/removeCompleted/{id}")
	public String remove(@PathVariable(value = "id") int id) {
		return alarmService.alarmRequest(id, BoxStatus.REMOVE_COMPLETED, "ROLE_ADMIN", AlarmType.REMOVE_COMPLETED);
	}

}
