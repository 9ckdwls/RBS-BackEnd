package com.example.rbs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.AlarmService;

@RestController
public class AlarmController {
	
	private AlarmService alarmService;
	
	public AlarmController(AlarmService alarmService) {
		this.alarmService = alarmService;
	}
	
	@GetMapping("test")
	public String test() {
		return "ok";
	}

}
