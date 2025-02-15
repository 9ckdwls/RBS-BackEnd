package com.example.rbs.service;

import org.springframework.stereotype.Service;

import com.example.rbs.repository.OrderItemsRepository;

@Service
public class OrderItemsService {

	private final OrderItemsRepository orderItemsRepository;
	
	public OrderItemsService(OrderItemsRepository orderItemsRepository) {
		this.orderItemsRepository = orderItemsRepository;
	}
}
