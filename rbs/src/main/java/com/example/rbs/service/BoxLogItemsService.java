package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.BoxLogItemsRepository;

@Service
public class BoxLogItemsService {
	
	private final BoxLogItemsRepository boxLogItemsRepository;
	
	public BoxLogItemsService(BoxLogItemsRepository boxLogItemsRepository) {
		this.boxLogItemsRepository = boxLogItemsRepository;
	}
}
