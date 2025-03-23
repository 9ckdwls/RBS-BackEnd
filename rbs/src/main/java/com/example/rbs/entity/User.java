package com.example.rbs.entity;

import java.util.Date;

import jakarta.persistence.Column;
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
	
	private String location;
	
	private String name;
	
	@Column(unique = true, nullable = false)
	private String phoneNumber;
	private int point;
	private Date date;
	
	private String role;
	
}
