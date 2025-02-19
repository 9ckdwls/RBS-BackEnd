package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Alarm;
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
	@PostMapping("admin/installRequest")
	public String installRequest(@RequestBody BoxDTO boxDTO) {
		return alarmService.installRequest(boxDTO);
	}

}
