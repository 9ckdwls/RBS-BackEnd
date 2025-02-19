package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.AlarmRepository;

@Service
public class AlarmService {
	
	private AlarmRepository alarmRepository;
	
	public AlarmService(AlarmRepository alarmRepository) {
		this.alarmRepository = alarmRepository;
	}

}
