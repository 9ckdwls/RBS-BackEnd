package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.service.BoxLogService;

@RestController
public class BoxLogController {

	private final BoxLogService boxLogService;

	public BoxLogController(BoxLogService boxLogService) {
		this.boxLogService = boxLogService;
	}
	
	// 모든 수거함로그 조회
	@GetMapping("admin/getBoxLog")
	public List<BoxLog> getBoxLog() {
		return boxLogService.getBoxLog();
	}

}
