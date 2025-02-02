package com.example.rbs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserDto {
	private String id;
	private String name;
    private String phoneNumber;
    private String pw;
}
