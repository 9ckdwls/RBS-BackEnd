package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.Item;
import com.example.rbs.service.ItemService;

@RestController
public class ItemController {
	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	// 상품 전부 가져오기
	@GetMapping("admin/getItems")
	public List<Item> getItems() {
		return itemService.getItems();
	}
}
