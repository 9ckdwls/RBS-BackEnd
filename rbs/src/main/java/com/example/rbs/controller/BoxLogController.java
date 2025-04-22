package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.BoxLogWithImageDto;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.service.BoxLogService;

@RestController
public class BoxLogController {

	private final BoxLogService boxLogService;

	public BoxLogController(BoxLogService boxLogService) {
		this.boxLogService = boxLogService;
	}

	// 수거 및 분리 내역
	@GetMapping("myBoxLog")
	public List<BoxLogWithImageDto> myBoxLog() {
		return boxLogService.myBoxLog();
	}
}
