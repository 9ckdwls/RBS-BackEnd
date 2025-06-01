package com.example.rbs.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CloseBoxUserResponseDTO {
	private String status;
	@JsonProperty("timestamp")
    private String timeStamp;
    private Map<String, Integer> result;
    private int boxId;
    private String userId;
    private int num;
    private String image;
    
    	
    // 기본 생성자
    public CloseBoxUserResponseDTO() {}
    
    public CloseBoxUserResponseDTO(String status) {
    	this.status = status;
    }
}
