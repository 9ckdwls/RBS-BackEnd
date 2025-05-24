package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.BoxLogResponse;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogItems;
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
	
	// 수거로그 id로 찾기
	public BoxLog findById(int boxLogId) {
		Optional<BoxLog> boxLog = boxLogRepository.findById(boxLogId);
		if(boxLog.isPresent()) {
			return boxLog.get();
		} else {
			return null;
		}
	}

	// 모든 수거함로그 조회
	public List<BoxLogResponse> getBoxLog() {
		List<BoxLog> boxLogList = boxLogRepository.findAll();
		return boxLogChange(boxLogList);
		
	}

	// userId로 수거함로그 검색
	public List<BoxLogResponse> findByUserId(String userId) {
		return boxLogChange(boxLogRepository.findByUserId(userId));
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

	// 수거 완료
	// 수거로그 작성
	public int collectionCompleted(int boxId, String saveFile) {
		BoxLog boxLog = new BoxLog();
		boxLog.setBoxId(boxId);
		boxLog.setCollection_file(saveFile);
		boxLog.setDate(new Date());
		boxLog.setType("수거");
		boxLog.setStatus("수거 후");
		boxLog.setUserId(userService.getUserId());
		
		
		List<BoxLog> boxLogList = boxLogRepository.findByBoxIdAndStaus(boxId, "수거 전");
		
		boxLog.setValue(0); // IOT 장비 제어 후 추가
		boxLogRepository.save(boxLog);
		
		boxLogItemsService.collectionCompleted(boxId, boxLog.getLogId(), boxLogList);
		
		return boxLog.getLogId();
	}

}
