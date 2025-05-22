package com.example.rbs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.dto.OrderResponse;
import com.example.rbs.entity.OrderDetails;
import com.example.rbs.repository.OrderDetailsRepository;;

@Service
public class OrderDetailsService {

	private final OrderDetailsRepository orderDetailsRepository;
	private final OrderItemsService orderItemsService;
	
	public OrderDetailsService(OrderDetailsRepository orderDetailsRepository, OrderItemsService orderItemsService) {
		this.orderDetailsRepository = orderDetailsRepository;
		this.orderItemsService = orderItemsService;
	}

	// 모든 사용자 주문 내역
	public List<OrderResponse> findOrderList() {
		return OrderChange(orderDetailsRepository.findAll());
	}

	// 사용자id로 주문 내역 조회
	public List<OrderResponse> findOrderListByUserId(String userId) {
		return OrderChange(orderDetailsRepository.findByUserId(userId));
	}
	
	private List<OrderResponse> OrderChange(List<OrderDetails> orderList) {
		List<OrderResponse> orderResponse = new ArrayList<>();
		for(OrderDetails order : orderList) {
			OrderResponse dto = new OrderResponse();
			dto.setOrder(order);
			dto.setItems(orderItemsService.findByOrderId(order.getId()));
			orderResponse.add(dto);
		}
		return orderResponse;
	}
}






