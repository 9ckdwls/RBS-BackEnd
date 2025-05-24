package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

	public void logUpdate(int boxId, Map<String, Integer> result, String saveFile) {
		BoxLog boxLog = new BoxLog();
		boxLog.setBoxId(boxId);
		boxLog.setType("분리");
		boxLog.setStatus("분리 중");
		boxLog.setUserId(userService.getId());
		
		String name;
		int count;
		
		if (result.containsKey("battery")) {
			name = "battery";
			count = result.get("battery");
			boxLog.setFile_battery(saveFile);
		} else if (result.containsKey("discharged")) {
			name = "discharged";
			count = result.get("discharged");
			boxLog.setFile_discharged(saveFile);
		} else if (result.containsKey("notDischarged")) {
			name = "notDischarged";
			count = result.get("notDischarged");
			boxLog.setFile_not_discharged(saveFile);
		} else {
			return;
		}
		boxLogRepository.save(boxLog);
		boxLogItemsService.saveLogItem(name, count, boxLog.getLogId());
		
	}

	// 수거함 사용 끝
	public String boxEnd(int boxId) {
		List<BoxLog> boxLogList = boxLogRepository.findByLogIdAndStatus(boxId, "분리 중");
		for(BoxLog boxLog : boxLogList) {
			boxLog.setDate(new Date());
			boxLog.setStatus("수거 전");
		}
		return "Success";
	}

		
}
