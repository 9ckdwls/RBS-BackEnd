package com.example.rbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IOTResponseDTO {
	private String status;
    private String message;
    
    public IOTResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
}