package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.OrderResponse;
import com.example.rbs.service.OrderDetailsService;

@RestController
public class OrderController {
	private final OrderDetailsService orderDetailsService;
	
	public OrderController(OrderDetailsService orderDetailsService) {
		this.orderDetailsService = orderDetailsService;
	}
	
	// 모든 사용자 주문 내역
	@GetMapping("admin/findOrderList")
	public List<OrderResponse> findOrderList() {
		return orderDetailsService.findOrderList();
	}
	
	// 사용자id로 주문 내역 조회
	@GetMapping("admin/findOrderListByUserId/{userId}")
	public List<OrderResponse> findOrderListByUserId(@PathVariable(value = "userId") String userId) {
		return orderDetailsService.findOrderListByUserId(userId);
	}
}
