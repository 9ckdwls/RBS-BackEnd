package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.OrderDetailsRepository;;

@Service
public class OrderDetailsService {

	private final OrderDetailsRepository orderDetailsRepository;
	
	public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
		this.orderDetailsRepository = orderDetailsRepository;
	}
}
