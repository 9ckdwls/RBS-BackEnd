package com.example.rbs.entity;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoxLog {
	
	@EmbeddedId
    private BoxLogId boxLogId; // 복합 키 클래스
	
	private int type; // 수거 : 0 분리 : 1
	
	private int weight; //무게
	
	private int point; // 포인트

}
