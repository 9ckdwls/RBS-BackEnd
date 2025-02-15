package com.example.rbs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.rbs.dto.OrderDTO;
import com.example.rbs.dto.OrderItemDTO;
import com.example.rbs.dto.OrderListResponseDTO;
import com.example.rbs.entity.Item;
import com.example.rbs.entity.OrderDetails;
import com.example.rbs.entity.OrderItems;
import com.example.rbs.repository.OrderDetailsRepository;

@Service
public class OrderDetailsService {

	private final OrderDetailsRepository orderDetailsRepository;
	private final UserService userService;
	private final ItemService itemService;
	private final OrderItemsService orderItemsService;

	public OrderDetailsService(OrderDetailsRepository orderDetailsRepository, UserService userService,
			ItemService itemService, OrderItemsService orderItemsService) {
		this.orderDetailsRepository = orderDetailsRepository;
		this.userService = userService;
		this.itemService = itemService;
		this.orderItemsService = orderItemsService;
	}

	// 장바구니에 상품 추가
	@Transactional
	public String basketAdd(List<OrderDTO> orderDTOList) {

		// 장바구니에 이미 아이템이 있는지 확인
		List<OrderDetails> Basket = orderDetailsRepository.findByUserIdAndState(userService.getId(), 0);
		OrderDetails myBasket;
		if (!Basket.isEmpty()) {
			// 존재한다면 해당 주문번호로 주문상품List 조회
			myBasket = Basket.get(0);
			List<OrderItems> orderItemsList = orderItemsService.findByOrderId(myBasket.getId());

			// 내가 추가로 주문한 상품이 이미 장바구니에 있는지 확인
			for (OrderDTO orderDTO : orderDTOList) { // 추가로 주문한 상품
				Item myItem;
				Optional<Item> item = itemService.findById(orderDTO.getItemId());
				if (item.isPresent()) {
					myItem = item.get();
				} else {
					return "Fail";
				}

				boolean itemFound = false;

				for (OrderItems orderItem : orderItemsList) { // 장바구니에 이미 있는 상품
					if (orderDTO.getItemId() == orderItem.getItemId()) { // 장바구니에 해당 상품이 있다면
						orderItem.setCount(orderItem.getCount() + orderDTO.getCount()); // 수량 합치기
						orderItemsService.save(orderItem);
						// 장바구니 totalPrice 업데이트
						myBasket.setTotalPrice(myBasket.getTotalPrice() + orderDTO.getCount() * myItem.getPrice());
						itemFound = true;
						break;
					}
				}

				if (!itemFound) {
					// 장바구니에 없는 상품이라면 새로 추가
					OrderItems orderItems = new OrderItems();
					orderItems.setOrderId(myBasket.getId());
					orderItems.setItemId(myItem.getId());
					orderItems.setCount(orderDTO.getCount());
					orderItems.setPrice(myItem.getPrice());
					orderItemsService.save(orderItems);

					myBasket.setTotalPrice(myBasket.getTotalPrice() + orderDTO.getCount() * myItem.getPrice());
				}
			}

			orderDetailsRepository.save(myBasket);
			return "Success";
		} else {
			int totalPrice = 0;

			OrderDetails orderDetails = new OrderDetails();
			orderDetails.setState(0);
			orderDetails.setUserId(userService.getId());

			orderDetailsRepository.save(orderDetails);

			for (OrderDTO orderDTO : orderDTOList) {
				OrderItems orderItems = new OrderItems();
				Optional<Item> item = itemService.findById(orderDTO.getItemId());

				if (item.isPresent()) {
					Item myItem = item.get();

					orderItems.setOrderId(orderDetails.getId());
					orderItems.setItemId(myItem.getId());
					orderItems.setCount(orderDTO.getCount());
					orderItems.setPrice(myItem.getPrice());

					totalPrice += myItem.getPrice() * orderDTO.getCount();

					orderItemsService.save(orderItems);
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return "Fail";
				}
			}

			orderDetails.setTotalPrice(totalPrice);
			orderDetailsRepository.save(orderDetails);

			return "Success";
		}

	}

	// 내 장바구니 보기
	public List<OrderListResponseDTO> myBakset(int state) {
		// 내 장바구니 가져오기
		List<OrderDetails> orderList = orderDetailsRepository.findByUserIdAndState(userService.getId(), state);

		// 응답 List<OrderListResponseDTO> 생성
		List<OrderListResponseDTO> orderListResponseDTOList = new ArrayList<>();

		// 장바구니는 1번, 주문내역은 그만큼
		for (OrderDetails order : orderList) {
			// List에 담을 OrderListResponseDTO 생성
			OrderListResponseDTO orderListResponseDTO = new OrderListResponseDTO();

			// OrderListResponseDTO에 값 넣기
			orderListResponseDTO.setId(order.getId());
			orderListResponseDTO.setUserId(order.getUserId());
			orderListResponseDTO.setDate(order.getDate());
			orderListResponseDTO.setState(order.getState());
			orderListResponseDTO.setTotalPrice(order.getTotalPrice());

			// 각 주문의 상품들 가져오기
			List<OrderItems> orderItems = orderItemsService.findByOrderId(order.getId());

			// OrderListResponseDTO의 List<OrderItemDTO> 생성
			List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

			// List<OrderItemDTO>에 들어갈 상품만큼 반복
			for (OrderItems orderItem : orderItems) {
				// List<OrderItemDTO>에 넣을 OrderItemDTO 생성
				OrderItemDTO orderItemDTO = new OrderItemDTO();

				orderItemDTO.setItemId(orderItem.getItemId());
				orderItemDTO.setPrice(orderItem.getPrice());
				orderItemDTO.setCount(orderItem.getCount());

				orderItemDTOList.add(orderItemDTO);
			}

			orderListResponseDTO.setOrderItems(orderItemDTOList);

			// List에 OrderListResponseDTO 추가
			orderListResponseDTOList.add(orderListResponseDTO);
		}
		return orderListResponseDTOList;
	}

	// 장바구니에서 주문
	public String order() {
		List<OrderDetails> orderList = orderDetailsRepository.findByUserIdAndState(userService.getId(), 0);
		if (!orderList.isEmpty()) {
			OrderDetails orderDetails = orderList.get(0);
			orderDetails.setDate(new Date());
			orderDetails.setState(1);
			orderDetailsRepository.save(orderDetails);
			return "Success";
		} else {
			return "Fail";
		}
	}

	// 장바구니 상품 수량 조절 0이면 삭제
	@Transactional
	public String basketAdjust(List<OrderDTO> orderDTOList) {
		List<OrderDetails> Basket = orderDetailsRepository.findByUserIdAndState(userService.getId(), 0);
		if (Basket.isEmpty()) {
			return "Fail";
		} else {
			// 내 주문
			OrderDetails orderDetails = Basket.get(0);
			// 주문 상품 목록
			List<OrderItems> orderItemsList = orderItemsService.findByOrderId(orderDetails.getId());

			// OrderDetails 가격 및 OrderItems 수량 조절
			for (OrderItems orderItems : orderItemsList) {
				for (OrderDTO orderDTO : orderDTOList) {
					// 상품 찾기
					Item myItem;
					Optional<Item> item = itemService.findById(orderItems.getItemId());
					if (item.isEmpty()) {
						return "Fail";
					} else {
						myItem = item.get();
						// 상품 목록이 수정하고자 하는 상품과 같다면
						if (orderItems.getItemId() == orderDTO.getItemId()) {
							// 수정될 개수가 음수라면 오류
							if (orderDTO.getCount() < 0) {
								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
								return "Fail";
								// 수정될 개수가 0 이라면 삭제
							} else {								
								// 임시 전체 가격 = 기존 가격 - 기존 수량 * 해당 아이템 가격
								orderDetails.setTotalPrice(
										orderDetails.getTotalPrice() - orderItems.getCount() * myItem.getPrice());
								// 최종 전체 가격 = 변경된 가격 + 수정될 개수 * 해당 아이템 가격
								orderDetails.setTotalPrice(
										orderDetails.getTotalPrice() + orderDTO.getCount() * myItem.getPrice());
								orderDetailsRepository.save(orderDetails);
								
								orderItems.setCount(orderDTO.getCount());

								if (orderDTO.getCount() == 0) {
									orderItemsService.deleteById(orderItems.getId());
								} else {
									orderItemsService.save(orderItems);
								}
							}
						}
					}
				}
			}
			
			// 장바구니가 비었으면 OrderDetails 삭제
			if(orderItemsService.findByOrderId(Basket.get(0).getId()).isEmpty()) {
				orderDetailsRepository.deleteById(Basket.get(0).getId());
			}
		}
		return "Success";
	}
}
