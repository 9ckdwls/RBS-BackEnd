package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.BoxLogItemsService;

@RestController
public class BoxLogItemsControll {
	
	private final BoxLogItemsService boxLogItemsService;
	
	public BoxLogItemsControll(BoxLogItemsService boxLogItemsService) {
		this.boxLogItemsService = boxLogItemsService;
	}
}
