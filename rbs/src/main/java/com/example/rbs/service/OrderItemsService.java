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

	// OrderItems에 항목 추가
	public void save(OrderItems orderItems) {
		orderItemsRepository.save(orderItems);
	}

	// orderId로 목록 찾기
	public List<OrderItems> findByOrderId(int id) {
		return orderItemsRepository.findByOrderId(id);
	}

	// id로 삭제
	public void deleteById(int id) {
		orderItemsRepository.deleteById(id);
	}
}
