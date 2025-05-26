package com.example.rbs.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CloseBoxResponseDTO {
	private String status;
	@JsonProperty("timestamp")
    private String timeStamp;
    private Map<String, Integer> result;
    private String image;
    private int boxId;
    
    	
    // 기본 생성자
    public CloseBoxResponseDTO() {}
    
    public CloseBoxResponseDTO(String status) {
    	this.status = status;
    }
}