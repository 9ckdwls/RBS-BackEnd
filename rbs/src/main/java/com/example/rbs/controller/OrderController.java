package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.dto.OrderDTO;
import com.example.rbs.dto.OrderListResponseDTO;
import com.example.rbs.service.OrderDetailsService;

@RestController
public class OrderController {
	private final OrderDetailsService orderDetailsService;

	public OrderController(OrderDetailsService orderDetailsService) {
		this.orderDetailsService = orderDetailsService;
	}

	// 장바구니에 상품 추가
	@PostMapping("user/basketAdd")
	public String basketAdd(@RequestBody List<OrderDTO> orderDTOList) {
		return orderDetailsService.basketAdd(orderDTOList);
	}

	// 내 장바구니 보기
	@GetMapping("user/myBasket")
	public List<OrderListResponseDTO> mybasket() {
		return orderDetailsService.myBakset(0);
	}

	// 주문내역 보기
	@GetMapping("user/myOrderList")
	public List<OrderListResponseDTO> myOrderList() {
		return orderDetailsService.myBakset(1);
	}

	// 장바구니에서 주문
	@PatchMapping("user/order")
	public String order() {
		return orderDetailsService.order();
	}

	// 장바구니 상품 수량 조절 0이면 삭제
	@PatchMapping("user/basketAdjust")
	public String basketAdjust(@RequestBody List<OrderDTO> orderDTOList) {
		return orderDetailsService.basketAdjust(orderDTOList);
	}

}
