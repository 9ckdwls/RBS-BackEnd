package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.BoxLogService;

@RestController
public class BoxLogController {

	private final BoxLogService boxLogService;

	public BoxLogController(BoxLogService boxLogService) {
		this.boxLogService = boxLogService;
	}

}
