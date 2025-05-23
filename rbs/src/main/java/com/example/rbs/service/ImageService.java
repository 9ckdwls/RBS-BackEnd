package com.example.rbs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.BoxLog;

@Service
public class ImageService {

	private final BoxService boxService;
	private final AlarmService alarmService;
	private final BoxLogService boxLogService;

	public ImageService(BoxService boxService, AlarmService alarmService, BoxLogService boxLogService) {
		this.boxService = boxService;
		this.alarmService = alarmService;
		this.boxLogService = boxLogService;
	}

	// 수거함 이미지
	public byte[] getBoxImage(int boxId) {
		return getImage(boxService.findById(boxId).getFile());
	}

	// 화재처리 이미지
	public byte[] getFireImage(int alarmId) {
		return getImage(alarmService.findById(alarmId).getFile());
	}

	// 수거 이미지
	public byte[] getCollectionImage(int boxLogId) {
		return getImage(boxLogService.findById(boxLogId).getCollection_file());
	}

	// 분리 배터리 이미지
	public byte[] getBatteryImage(int boxLogId) {
		BoxLog log = boxLogService.findById(boxLogId);
		if (log.getFile_battery() != null) {
			return getImage(log.getFile_battery());
		} else {
			return null;
		}
	}

	// 분리 방전된 폐전지 이미지
	public byte[] getDischargedImage(int boxLogId) {
		BoxLog log = boxLogService.findById(boxLogId);
		if (log.getFile_discharged() != null) {
			return getImage(log.getFile_discharged());
		} else {
			return null;
		}
	}

	// 분리 방전되지 않은 폐전지 이미지
	public byte[] getUndischargedImage(int boxLogId) {
		BoxLog log = boxLogService.findById(boxLogId);
		if (log.getFile_not_discharged() != null) {
			return getImage(log.getFile_not_discharged());
		} else {
			return null;
		}
	}

	// 사진 파일 얻기
	public byte[] getImage(String file) {

		try {
			Path path = Paths.get("C:/uploads/images/" + file);
			if (!Files.exists(path)) {
				return null;
			}
			byte[] data = Files.readAllBytes(path);

			return data;
		} catch (IOException e) {
			return null;
		}
	}

}