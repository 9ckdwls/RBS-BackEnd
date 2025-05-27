package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.Box;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.rbs.dto.BoxLogResponse;
import com.example.rbs.dto.CloseBoxResponseDTO;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {

	private final BoxLogRepository boxLogRepository;
	private final UserService userService;
	private final BoxLogItemsService boxLogItemsService;
	@Value("${battery}")
	private int battery;
	@Value("${discharged}")
	private int discharged;
	@Value("${notDischarged}")
	private int notDischarged;

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

	public int logUpdate(int boxId, Map<String, Integer> result, String saveFile) {
		Optional<BoxLog> opBoxlog = boxLogRepository.findByLogIdAndStatus(boxId, "분리 중");
		BoxLog boxLog;
		if(opBoxlog.isEmpty()) {
			boxLog = new BoxLog();
			boxLog.setBoxId(boxId);
			boxLog.setType("분리");
			boxLog.setStatus("분리 중");
			boxLog.setUserId(userService.getId());
		} else {
			boxLog = opBoxlog.get();
		}
		
		
		String name;
		int count;
		
		if (result.containsKey("battery")) {
			name = "battery";
			count = result.get("battery");
			boxLog.setValue(boxLog.getValue() + count * battery);
			boxLog.setFile_battery(saveFile);
		} else if (result.containsKey("discharged")) {
			name = "discharged";
			count = result.get("discharged");
			boxLog.setValue(boxLog.getValue() + count * discharged);
			boxLog.setFile_discharged(saveFile);
		} else if (result.containsKey("notDischarged")) {
			name = "notDischarged";
			count = result.get("notDischarged");
			boxLog.setValue(boxLog.getValue() + count * notDischarged);
			boxLog.setFile_not_discharged(saveFile);
		} else {
			return 0;
		}
		boxLogRepository.save(boxLog);
		boxLogItemsService.saveLogItem(name, count, boxLog.getLogId());
		
		return count * 5;
		
	}

	// 수거함 사용 끝
	public int boxEnd(int boxId) {
		Optional<BoxLog> boxlog = boxLogRepository.findByLogIdAndStatus(boxId, "분리 중");
		if(boxlog.isPresent()) {
			BoxLog myBoxLog = boxlog.get();
			myBoxLog.setDate(new Date());
			myBoxLog.setStatus("수거 전");
			return myBoxLog.getValue();
		}
		return 0;
	}

	// 익명 사용자 수거함 이용
	public String boxUse(CloseBoxResponseDTO dto, String saveFile) {
		BoxLog boxLog = new BoxLog();
		boxLog = new BoxLog();
		boxLog.setBoxId(dto.getBoxId());
		boxLog.setDate(new Date());
		boxLog.setType("분리");
		boxLog.setStatus("수거 전");
		boxLog.setUserId("익명의 사용자");
		
		Map<String, Integer> result = dto.getResult();
		String name;
		int count=0;
		
		if (result.containsKey("battery")) {
			name = "battery";
			count = result.get("battery");
			boxLog.setValue(boxLog.getValue() + count * 5);
			boxLog.setFile_battery(saveFile);
		} else if (result.containsKey("discharged")) {
			name = "discharged";
			count = result.get("discharged");
			boxLog.setValue(boxLog.getValue() + count * 10);
			boxLog.setFile_discharged(saveFile);
		} else if (result.containsKey("notDischarged")) {
			name = "notDischarged";
			count = result.get("notDischarged");
			boxLog.setValue(boxLog.getValue() + count * 15);
			boxLog.setFile_not_discharged(saveFile);
		} else {
			return "Fail";
		}
		boxLogRepository.save(boxLog);
		boxLogItemsService.saveLogItem(name, count, boxLog.getLogId());
		return null;
	}

		
}
