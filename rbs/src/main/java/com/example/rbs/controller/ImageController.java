package com.example.rbs.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.ImageService;

@RestController
public class ImageController {
	
	private final ImageService imageService;
	
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}
	
	// 수거함 이미지
	@GetMapping(value = "admin/boxImage/{boxId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getBoxImage(@PathVariable(value = "boxId") int boxId) {
		return imageService.getBoxImage(boxId);
	}
	
	// 화재처리 이미지
	@GetMapping(value = "admin/fireImage/{alarmId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getFireImage(@PathVariable(value = "alarmId") int alarmId) {
		return imageService.getFireImage(alarmId);
	}
	
	// 수거 이미지
	@GetMapping(value = "admin/collectionImage/{boxLogId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getCollectionImage(@PathVariable(value = "boxLogId") int boxLogId) {
		return imageService.getCollectionImage(boxLogId);
	}
	
	// 분리 이미지
	// map에 battery, discharged, notDischarged를 key로 각 배터리, 방전된 폐전지, 방전되지 않은 폐전지
	@GetMapping(value = "admin/itemsImage/{boxLogId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Map<String, byte[]> getItemsImage(@PathVariable(value = "boxLogId") int boxLogId) {
		return imageService.getItemsImage(boxLogId);
	}
	

}