package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.BoxLogResponse;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {

	private final BoxLogRepository boxLogRepository;
	private final UserService userService;
	private final BoxLogItemsService boxLogItemsService;

	public BoxLogService(BoxLogRepository boxLogRepository, UserService userService, BoxLogItemsService boxLogItemsService) {
		this.boxLogRepository = boxLogRepository;
		this.userService = userService;
		this.boxLogItemsService = boxLogItemsService;
	}
	
	public BoxLog findById(int boxLogId) {
		Optional<BoxLog> boxLog = boxLogRepository.findById(boxLogId);
		if(boxLog.isPresent()) {
			return boxLog.get();
		} else {
			return null;
		}
	}

	// 수거 및 분리 내역
	public List<BoxLogResponse> myBoxLog() {
		List<BoxLog> boxLogList = boxLogRepository.findByUserId(userService.getId());
		return boxLogChange(boxLogList);
	}
	
	private List<BoxLogResponse> boxLogChange(List<BoxLog> boxLogList) {
		List<BoxLogResponse> boxLogResponse = new ArrayList<>();
		for(BoxLog boxLog : boxLogList) {
			BoxLogResponse dto = new BoxLogResponse();
			dto.setBoxLog(boxLog);
			dto.setItems(boxLogItemsService.getBoxLogItems(boxLog.getLogId()));
			
			boxLogResponse.add(dto);
		}
		return boxLogResponse;
	}
}
