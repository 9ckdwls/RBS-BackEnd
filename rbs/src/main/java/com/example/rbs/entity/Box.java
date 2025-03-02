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
	
	private int volume;
	
	@Enumerated(EnumType.STRING)
	private FireStatus fireStatus;
	
	@Enumerated(EnumType.STRING)
	private UsageStatus usageStatus;
	
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