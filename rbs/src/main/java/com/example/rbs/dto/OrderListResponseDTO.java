package com.example.rbs.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListResponseDTO {
	private int id;
    private String userId;
    private int totalPrice;
    private int state;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    
    private List<OrderItemDTO> orderItems;

}
