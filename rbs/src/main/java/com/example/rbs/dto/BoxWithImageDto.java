package com.example.rbs.dto;

import com.example.rbs.entity.Box;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxWithImageDto {
	private Box box;
	private String imageBase64;
}