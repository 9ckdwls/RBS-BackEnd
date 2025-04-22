package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.AlarmWithImageDto;
import com.example.rbs.dto.BoxLogWithImageDto;
import com.example.rbs.entity.Alarm;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {
	
	private final BoxLogRepository boxLogRepository;
	private final UserService userService;
	private final BoxLogItemsService boxLogItemsService;
	private final FileService fileService;

	public BoxLogService(BoxLogRepository boxLogRepository, UserService userService, BoxLogItemsService boxLogItemsService,
			FileService fileService) {
		this.boxLogRepository = boxLogRepository;
		this.userService = userService;
		this.boxLogItemsService = boxLogItemsService;
		this.fileService = fileService;
	}

	// 모든 수거함로그 조회
	public List<BoxLogWithImageDto> getBoxLog() {
		List<BoxLog> boxLogs = boxLogRepository.findAll();
		List<BoxLogWithImageDto> result = new ArrayList<>();
		
		for (BoxLog boxLog : boxLogs) {
			BoxLogWithImageDto dto = new BoxLogWithImageDto();
            dto.setBoxLog(boxLog);
            
            dto.setImageBattery(encodeImage(boxLog.getFile_battery()));
            dto.setImageDischarged(encodeImage(boxLog.getFile_discharged()));
            dto.setImageNotDischarged(encodeImage(boxLog.getFile_not_discharged()));
            dto.setImageCollection(encodeImage(boxLog.getCollection_file()));

            result.add(dto);
        }
		
        return result;
	}

	// userId로 수거함로그 검색
	public List<BoxLogWithImageDto> findByUserId(String userId) {
		List<BoxLog> boxLogs = boxLogRepository.findByUserId(userId);
		List<BoxLogWithImageDto> result = new ArrayList<>();

		for (BoxLog boxLog : boxLogs) {
			BoxLogWithImageDto dto = new BoxLogWithImageDto();
            dto.setBoxLog(boxLog);
            
            dto.setImageBattery(encodeImage(boxLog.getFile_battery()));
            dto.setImageDischarged(encodeImage(boxLog.getFile_discharged()));
            dto.setImageNotDischarged(encodeImage(boxLog.getFile_not_discharged()));
            dto.setImageCollection(encodeImage(boxLog.getCollection_file()));

            result.add(dto);
        }
		
        return result;
	}
	
	// 사진 파일 경로를 Base64이미지로 변환
	private String encodeImage(String filePath) {
        if (filePath == null || filePath.isBlank()) return null;

        try {
        	return fileService.loadImageFromPath(filePath);
        } catch (IOException e) {
            return null;
        }
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
