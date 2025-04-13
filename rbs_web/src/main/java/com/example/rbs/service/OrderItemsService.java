package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.OrderItems;
import com.example.rbs.repository.OrderItemsRepository;

@Service
public class OrderItemsService {

	private final OrderItemsRepository orderItemsRepository;
	
	public OrderItemsService(OrderItemsRepository orderItemsRepository) {
		this.orderItemsRepository = orderItemsRepository;
	}

	// 주문번호로 주문아이템 검색
	public List<OrderItems> findByOrderId(int orderId) {
		return orderItemsRepository.findByOrderId(orderId);
	}
}
