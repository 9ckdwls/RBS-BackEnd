package com.example.rbs.dto;

import java.sql.Date;
import java.util.List;

import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.BoxLogItems;

import lombok.Data;

@Data
public class BoxLogResponse {
    private BoxLog boxLog;
    private List<BoxLogItems> items;
}