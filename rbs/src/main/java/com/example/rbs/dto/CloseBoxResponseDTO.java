package com.example.rbs.dto;

import lombok.Data;

@Data
public class CloseBoxResponseDTO {
	private String status;
    private String message;
    private String image;
    
    // 기본 생성자
    public CloseBoxResponseDTO() {}
    
    public CloseBoxResponseDTO(String status, String message, String image) {
        this.status = status;
        this.message = message;
        this.image = image;
    }
}
