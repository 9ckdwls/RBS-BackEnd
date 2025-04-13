package com.example.rbs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.rbs.entity.OrderDetails;
import com.example.rbs.repository.OrderDetailsRepository;;

@Service
public class OrderDetailsService {

	private final OrderDetailsRepository orderDetailsRepository;
	
	public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
		this.orderDetailsRepository = orderDetailsRepository;
	}

	// 모든 사용자 주문 내역
	public List<OrderDetails> findOrderList() {
		return orderDetailsRepository.findAll();
	}

	// 사용자id로 주문 내역 조회
	public List<OrderDetails> findOrderListByUserId(String userId) {
		return orderDetailsRepository.findByUserId(userId);
	}
}
