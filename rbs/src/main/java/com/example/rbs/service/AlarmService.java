package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;
import com.example.rbs.repository.AlarmRepository;

@Service
public class AlarmService {

	private AlarmRepository alarmRepository;
	private UserService userService;

	public AlarmService(AlarmRepository alarmRepository, UserService userService) {
		this.alarmRepository = alarmRepository;
		this.userService = userService;
	}

	// 화재처리 내역 보기
	public List<Alarm> fireLog() {
		return alarmRepository.findByResolvedAndTargetUserId(AlarmStatus.RESOLVED, userService.getId());
	}

}
