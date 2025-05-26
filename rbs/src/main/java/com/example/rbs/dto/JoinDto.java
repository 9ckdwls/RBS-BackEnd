package com.example.rbs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {
	
	private String id;
	private String pw;
	private String name;
	private String phoneNumber;
	private String verificationCode;
	private String location1;
	private String location2;
}
