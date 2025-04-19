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
    
    private String type;
    
	private int value;
	
	private String status;
	
	private String file_battery;
	
	private String file_discharged;
	
	private String file_not_discharged;
	
	private String collection_file;

}
