package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.AlarmWithImageDto;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.Alarm.AlarmStatus;
import com.example.rbs.repository.AlarmRepository;

@Service
public class AlarmService {

	private AlarmRepository alarmRepository;
	private UserService userService;
	private FileService fileService;

	public AlarmService(AlarmRepository alarmRepository, UserService userService, FileService fileService) {
		this.alarmRepository = alarmRepository;
		this.userService = userService;
		this.fileService = fileService;
	}

	// 화재처리 내역 보기
	public List<AlarmWithImageDto> fireLog() {
		List<Alarm> alarms = alarmRepository.findByResolvedAndTargetUserId(AlarmStatus.RESOLVED, userService.getId());
		List<AlarmWithImageDto> result = new ArrayList<>();
		for (Alarm alarm : alarms) {
			AlarmWithImageDto dto = new AlarmWithImageDto();
			dto.setAlarm(alarm);

			try {
				String base64Image = fileService.loadImageFromPath(alarm.getFile());
				dto.setImageBase64(base64Image);
			} catch (IOException e) {
				dto.setImageBase64(null); // 파일 없으면 null 처리
			}

			result.add(dto);
		}

		return result;
	}

}
