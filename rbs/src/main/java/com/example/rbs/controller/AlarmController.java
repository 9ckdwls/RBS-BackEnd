package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.Alarm;
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
	
	

}
