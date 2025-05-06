package com.example.rbs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

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
