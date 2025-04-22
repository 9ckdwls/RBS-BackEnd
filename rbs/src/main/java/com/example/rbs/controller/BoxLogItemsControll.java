package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogItems;
import com.example.rbs.service.BoxLogItemsService;

@RestController
public class BoxLogItemsControll {

	private final BoxLogItemsService boxLogItemsService;

	public BoxLogItemsControll(BoxLogItemsService boxLogItemsService) {
		this.boxLogItemsService = boxLogItemsService;
	}

	// 수거로그 아이디로 아이템 가져오기
	@GetMapping("admin/getBoxLogItems/{boxLogId}")
	public List<BoxLogItems> getBoxLogItems(@PathVariable(value = "boxLogId") int boxLogId) {
		return boxLogItemsService.getBoxLogItems(boxLogId);
	}
}
