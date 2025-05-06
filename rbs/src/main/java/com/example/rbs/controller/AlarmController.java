package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.AlarmWithImageDto;
import com.example.rbs.entity.Alarm;
import com.example.rbs.service.AlarmService;

@RestController
public class AlarmController {

	private AlarmService alarmService;

	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	// 화재처리 내역 보기
	@GetMapping("fireLog")
	public List<Alarm> fireLog() {
		return alarmService.fireLog();
	}

}
