package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.OrderItemsRepository;

@Service
public class OrderService {

	private final OrderItemsRepository orderRepository;

	public OrderService(OrderItemsRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
