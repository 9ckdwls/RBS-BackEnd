package com.example.rbs.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class FileService {
	// 파일 경로를 받아 이미지 데이터를 byte[]로 반환
    public String loadImageFromPath(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
        byte[] imageData = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(imageData);
    }
}
