package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.BoxLogRepository;

@Service
public class BoxLogService {
	
	private final BoxLogRepository boxLogRepository;

	public BoxLogService(BoxLogRepository boxLogRepository) {
		this.boxLogRepository = boxLogRepository;
	}

}
