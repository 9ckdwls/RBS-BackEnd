package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.OrderRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
