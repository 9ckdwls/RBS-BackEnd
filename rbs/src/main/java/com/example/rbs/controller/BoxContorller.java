package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.BoxService;

@RestController
public class BoxContorller {

	private final BoxService boxService;

	public BoxContorller(BoxService boxService) {
		this.boxService = boxService;
	}

	
	
}
