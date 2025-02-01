package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.ItemService;


@RestController
public class ItemController {
	private final ItemService itemService;
	
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
}
