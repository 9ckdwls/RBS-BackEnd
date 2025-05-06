package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {

	private final BoxLogRepository boxLogRepository;
	private final UserService userService;

	public BoxLogService(BoxLogRepository boxLogRepository, UserService userService) {
		this.boxLogRepository = boxLogRepository;
		this.userService = userService;
	}

	// 수거 및 분리 내역
	public List<BoxLog> myBoxLog() {
		List<BoxLog> boxLogs = boxLogRepository.findByUserId(userService.getId());
		return boxLogs;
	}
}
