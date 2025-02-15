package com.example.rbs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private int itemId;
    private int count;
    private int price;
}

