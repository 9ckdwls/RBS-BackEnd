package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.AlarmCheckService;

@RestController
public class AlarmCheckController {
	
	private AlarmCheckService alarmCheckService;
	
	public AlarmCheckController(AlarmCheckService alarmCheckService) {
		this.alarmCheckService = alarmCheckService;
	}
}
