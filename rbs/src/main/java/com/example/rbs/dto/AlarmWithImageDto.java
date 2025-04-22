package com.example.rbs.dto;

import com.example.rbs.entity.Alarm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmWithImageDto {
    private Alarm alarm;
    private String imageBase64;
}