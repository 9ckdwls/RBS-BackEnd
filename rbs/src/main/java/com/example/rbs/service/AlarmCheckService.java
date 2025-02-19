package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.AlarmCheckRepository;

@Service
public class AlarmCheckService {
	
	private AlarmCheckRepository alarmCheckRepository;
	
	public AlarmCheckService(AlarmCheckRepository alarmCheckRepository) {
		this.alarmCheckRepository = alarmCheckRepository;
	}

}
