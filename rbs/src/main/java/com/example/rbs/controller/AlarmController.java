package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.AlarmService;

@RestController
public class AlarmController {
	
	private AlarmService alarmService;
	
	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

}
