package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.entity.Box;
import com.example.rbs.service.BoxService;

@RestController
public class BoxContorller {

	private final BoxService boxService;

	public BoxContorller(BoxService boxService) {
		this.boxService = boxService;
	}

	// 모든 수거함 조회
	@GetMapping("findAllBox")
	public List<Box> findAllBox() {
		return boxService.findAllBox();
	}
	
	// 수거함 id로 검색
	@GetMapping("findBoxById/{id}")
	public Box findBoxById(@PathVariable(value = "id") int id) {
		return boxService.findBoxById(id);
	}

	// 수거함 이름으로 검색
	@GetMapping("findBoxByName/{name}")
	public Box findBoxByName(@PathVariable(value = "name") String name) {
		return boxService.findBoxByName(name);
	}

	// 수거함 문열기
	// 테스트 불가 IOT 장비 연결 후 테스트
	@GetMapping("boxOpen/{id}")
	public String boxOpen(@PathVariable(value = "role") String role, @PathVariable(value = "id") int id) {
		return boxService.boxControll("open", id);
	}

	// 수거함 문닫기
	// 테스트 불가 IOT 장비 연결 후 테스트
	@GetMapping("boxClose/{id}")
	public String boxClose(@PathVariable(value = "role") String role, @PathVariable(value = "id") int id) {
		return boxService.boxControll("close", id);
	}

}
