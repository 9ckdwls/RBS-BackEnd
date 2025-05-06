package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	// userId로 수거함 로그 검색
	@GetMapping("admin/findBoxLogById/{userId}")
	public List<BoxLog> findBoxLogById(@PathVariable(value = "userId") String userId) {
		return boxLogService.findByUserId(userId);
	}

}
