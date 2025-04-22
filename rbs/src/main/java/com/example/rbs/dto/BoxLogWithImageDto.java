package com.example.rbs.dto;

import com.example.rbs.entity.BoxLog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxLogWithImageDto {
	private BoxLog boxLog;
	private String imageBattery;
    private String imageDischarged;
    private String imageNotDischarged;
    private String imageCollection;
}
