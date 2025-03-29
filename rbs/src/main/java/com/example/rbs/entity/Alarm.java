package com.example.rbs.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Alarm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int boxId;
	
	private String userId;
	
    private String targetUserId;
		
	@Enumerated(EnumType.STRING)
	private AlarmStatus resolved;
	
	@Enumerated(EnumType.STRING)
	private AlarmType type;
	
	private String role;
	
	private Date date;
	
	public enum AlarmStatus {
        UNRESOLVED, IN_PROGRESS, RESOLVED
    }

	public enum AlarmType {
	    COLLECTION_NEEDED,          // 수거 필요
	    COLLECTION_RECOMMENDED,     // 수거 권장
	    FIRE,                       // 화재 발생
	    INSTALL_REQUEST,            // 설치 요청
	    INSTALL_IN_PROGRESS,        // 설치 진행 중
	    INSTALL_COMPLETED,          // 설치 완료
	    INSTALL_CONFIRMED,          // 설치 확정
	    REMOVE_REQUEST,             // 제거 요청
	    REMOVE_IN_PROGRESS,         // 제거 진행 중
	    REMOVE_COMPLETED,           // 제거 완료
	    REMOVE_CONFIRMED            // 제거 확정
	}
}