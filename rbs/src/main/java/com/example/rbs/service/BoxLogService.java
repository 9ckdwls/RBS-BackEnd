package com.example.rbs.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

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

	// 모든 수거함로그 조회
	public List<BoxLog> getBoxLog() {
		return boxLogRepository.findAll();
	}

	// userId로 수거함로그 검색
	public List<BoxLog> findByUserId(String userId) {
		return boxLogRepository.findByUserId(userId);
	}

	// 수거 완료
	// 수거로그 작성
	public void collectionCompleted(int boxId, String saveFile) {
		BoxLog boxLog = new BoxLog();
		boxLog.setBoxId(boxId);
		boxLog.setCollection_file(saveFile);
		boxLog.setDate(new Date());
		boxLog.setType("수거");
		boxLog.setUserId(userService.getUserId());
		boxLog.setValue(0); // IOT 장비 제어 후 추가
		boxLogRepository.save(boxLog);
		
		List<BoxLog> boxLogList = boxLogRepository.findByBoxIdAndStatus(boxId, "수거 전");
		
		// 수거로그 아이템
		boxLogItemsService.collectionCompleted(boxLog.getBoxId(), boxLog.getLogId(), boxLogList);
	}

}
