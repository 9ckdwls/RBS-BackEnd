package com.example.rbs.entity;


import java.sql.Date;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoxLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;
	
	private int boxId;
	
    private String userId;
    
    private Date date;
	
	private int type; // 수거 : 0 분리 : 1
	
	private int value; // 일반사용자는 point, 수거자는 수익금

}
