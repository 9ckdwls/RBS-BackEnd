package com.example.rbs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.BoxLogWithImageDto;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {

	private final BoxLogRepository boxLogRepository;
	private final UserService userService;
	private final FileService fileService;

	public BoxLogService(BoxLogRepository boxLogRepository, UserService userService, FileService fileService) {
		this.boxLogRepository = boxLogRepository;
		this.userService = userService;
		this.fileService = fileService;
	}

	// 수거 및 분리 내역
	public List<BoxLogWithImageDto> myBoxLog() {
		List<BoxLog> boxLogs = boxLogRepository.findByUserId(userService.getId());
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
		if (filePath == null || filePath.isBlank())
			return null;

		try {
			return fileService.loadImageFromPath(filePath);
		} catch (IOException e) {
			return null;
		}
	}

}
