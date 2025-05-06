package com.example.rbs.controller;

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
	
	// 사진 파일 얻기
	@GetMapping(value = "image/{file:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getImage(@PathVariable(value = "file") String file) {
		return imageService.getImage(file);
	}

}
