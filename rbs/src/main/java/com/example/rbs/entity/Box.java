package com.example.rbs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	
	private int volume;
	
	private int fire;
	
	private int used;
	
	//반환되는 JSON 값을 문자열로 바꾸기
	@JsonProperty("location")
    public String getLocationAsText() {
        return location != null ? location.toText() : null;
    }
}
