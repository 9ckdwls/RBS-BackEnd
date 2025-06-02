package com.example.rbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rbs.entity.OrderDetails;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {


	List<OrderDetails> findByState(int i);

	List<OrderDetails> findByUserIdAndState(String userId, int i);

}
