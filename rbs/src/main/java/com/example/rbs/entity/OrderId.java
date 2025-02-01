package com.example.rbs.entity;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class OrderId implements Serializable {
	
	private int orderNumber;
	private int itemId;
	
	public OrderId() {
	}
	
	public OrderId(int orderNumber, int itemId) {
		this.orderNumber = orderNumber;
		this.itemId = itemId;
	}
}
