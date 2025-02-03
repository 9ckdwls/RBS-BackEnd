package com.example.rbs.entity;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Embeddable;

@Embeddable
public class BoxLogId implements Serializable {

    private int boxId;
    private String userId;
    private Date date;

    // 기본 생성자, equals(), hashCode() 메서드 필요
    public BoxLogId() {}

    public BoxLogId(int boxId, String userId, Date date) {
        this.boxId = boxId;
        this.userId = userId;
        this.date = date;
    }
    
}
