package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.OrderDetailsService;

@RestController
public class OrderController {
	private final OrderDetailsService orderDetailsService;
	
	public OrderController(OrderDetailsService orderDetailsService) {
		this.orderDetailsService = orderDetailsService;
	}
}
