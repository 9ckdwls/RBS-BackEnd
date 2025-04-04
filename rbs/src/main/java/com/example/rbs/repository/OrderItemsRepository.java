package com.example.rbs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rbs.entity.OrderDetails;
import com.example.rbs.entity.OrderItems;


@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {

}
