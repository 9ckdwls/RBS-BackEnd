package com.example.rbs.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
	@Id
	private String id;
	private String pw;
	
	private String name;
	private String phoneNumber;
	private int point;
	private Date date;
	
	private String role;
	
}
