package com.example.rbs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IOTResponseDTO {
	private String status;
    
    public IOTResponseDTO(String status) {
        this.status = status;
    }
}
