package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Alarm;
import com.example.rbs.repository.AlarmRepository;

@Service
public class AlarmService {
	
	private AlarmRepository alarmRepository;
	private BoxService boxService;
	
	public AlarmService(AlarmRepository alarmRepository, BoxService boxService) {
		this.alarmRepository = alarmRepository;
		this.boxService = boxService;
	}

	// 미해결된 알람 가져오기
	public List<Alarm> unResolved() {
		return alarmRepository.findByResolved(false);
	}

	// 수거함 설치 요청
	public String installRequest(BoxDTO boxDTO) {
		return boxService.installRequest(boxDTO);
	}

}
