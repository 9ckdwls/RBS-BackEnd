package com.example.rbs.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
public class Box {
	@EmbeddedId
    private BoxId boxId_tset_user; // 복합 키 클래스
	
	//master 공통된 코드 수정
	
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
