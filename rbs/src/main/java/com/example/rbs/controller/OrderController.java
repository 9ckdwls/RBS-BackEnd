package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.OrderService;

@RestController
public class OrderController {
	private final OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
}
