package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.OrderItems;
import com.example.rbs.service.OrderItemsService;

@RestController
public class OrderItemsController {
	
	private final OrderItemsService orderItemsService;
	
	public OrderItemsController (OrderItemsService orderItemsService) {
		this.orderItemsService = orderItemsService;
	}
	
	// 주문번호로 주문아이템 검색
	@GetMapping("admin/findByOrderId/{orderId}")
	public List<OrderItems> findByOrderId(@PathVariable(value = "orderId") int orderId) {
		return orderItemsService.findByOrderId(orderId);
	}
}
