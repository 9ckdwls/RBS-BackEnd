package com.example.rbs.dto;

import java.util.List;

import com.example.rbs.entity.OrderDetails;
import com.example.rbs.entity.OrderItems;


import lombok.Data;

@Data
public class OrderResponse {
	private OrderDetails order;
	private List<OrderItems> items;
}
