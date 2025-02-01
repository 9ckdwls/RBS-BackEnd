package com.example.rbs.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class BoxId implements Serializable {

    private int id;
    private String name;

    // 기본 생성자, equals(), hashCode() 메서드 필요
    public BoxId() {}

    public BoxId(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
}
