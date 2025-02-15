package com.example.rbs.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.OrderDetails;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {


	// 회원 id와 주문상태로 주문내역 조회
	List<OrderDetails> findByUserIdAndState(String userId, int state);


}
