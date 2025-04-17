package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {
	
	private final BoxLogRepository boxLogRepository;

	public BoxLogService(BoxLogRepository boxLogRepository) {
		this.boxLogRepository = boxLogRepository;
	}

	// 모든 수거함로그 조회
	public List<BoxLog> getBoxLog() {
		return boxLogRepository.findAll();
	}

	// userId로 수거함로그 검색
	public List<BoxLog> findByUserId(String userId) {
		return boxLogRepository.findByUserId(userId);
	}

}
