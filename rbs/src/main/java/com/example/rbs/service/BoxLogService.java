package com.example.rbs.service;

import com.example.rbs.repository.BoxLogRepository;

public class BoxLogService {
	
	private final BoxLogRepository boxLogRepository;

	public BoxLogService(BoxLogRepository boxLogRepository) {
		this.boxLogRepository = boxLogRepository;
	}

}
