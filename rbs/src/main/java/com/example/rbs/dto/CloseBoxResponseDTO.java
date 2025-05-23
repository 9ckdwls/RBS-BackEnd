package com.example.rbs.dto;

import lombok.Data;

@Data
public class CloseBoxResponseDTO {
	private String status;
	private String timestamp;
	private int result;
    private String image;
    
    // 기본 생성자
    public CloseBoxResponseDTO() {}
    
    public CloseBoxResponseDTO(String status) {
        this.status = status;
    }
}
