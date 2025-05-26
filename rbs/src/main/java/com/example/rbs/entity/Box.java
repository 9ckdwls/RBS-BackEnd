package com.example.rbs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
public class Box {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
	@Column(unique = true, nullable = false)
	private String name;
	
	private String IPAddress;
	
	private Point location;
	
	@Enumerated(EnumType.STRING)
	private InstallStatus installStatus;
	
	private int volume1;
	
	private int volume2;
	
	private int volume3;
	
	@Enumerated(EnumType.STRING)
	private FireStatus fireStatus1;
	
	@Enumerated(EnumType.STRING)
	private FireStatus fireStatus2;
	
	@Enumerated(EnumType.STRING)
	private FireStatus fireStatus3;
	
	@Enumerated(EnumType.STRING)
	private UsageStatus usageStatus;
	
	private int store1;
	
	private int store2;
	
	private int store3;
	
	private int store4;
	
	private String file;
	
	//반환되는 JSON 값을 문자열로 바꾸기
	@JsonProperty("location")
    public String getLocationAsText() {
        return location != null ? location.toText() : null;
    }
	
	public enum InstallStatus {
        INSTALL_REQUEST, 
        INSTALL_IN_PROGRESS, 
        INSTALL_COMPLETED, 
        INSTALL_CONFIRMED,
        REMOVE_REQUEST, 
        REMOVE_IN_PROGRESS, 
        REMOVE_COMPLETED, 
        REMOVE_CONFIRMED
    }

    public enum UsageStatus {
        AVAILABLE, BLOCKED, USED
    }
    
    public enum FireStatus {
    	FIRE, UNFIRE
    }

}

