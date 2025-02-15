package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.service.OrderItemsService;

@RestController
public class OrderItemsController {
	
	private final OrderItemsService orderItemsService;
	
	public OrderItemsController (OrderItemsService orderItemsService) {
		this.orderItemsService = orderItemsService;
	}
}
