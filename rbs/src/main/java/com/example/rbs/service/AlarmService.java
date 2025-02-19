package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Alarm;
import com.example.rbs.repository.AlarmRepository;

@Service
public class AlarmService {
	
	private AlarmRepository alarmRepository;
	
	public AlarmService(AlarmRepository alarmRepository) {
		this.alarmRepository = alarmRepository;
	}

	// 미해결된 알람 가져오기
	public List<Alarm> unResolved() {
		return alarmRepository.findByResolved(false);
	}

}
