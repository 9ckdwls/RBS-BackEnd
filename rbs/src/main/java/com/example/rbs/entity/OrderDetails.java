package com.example.rbs.entity;

import java.util.Date;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderDetails {
	
	@EmbeddedId
	private OrderId orderId;
	
	private String userId;
	
	private int count;
	
	private int price;
	
	private int state;
	
	private Date date;
	
}
