package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.BoxDTO;
import com.example.rbs.entity.Box;
import com.example.rbs.service.BoxService;

@RestController
public class BoxContorller {

	private final BoxService boxService;

	public BoxContorller(BoxService boxService) {
		this.boxService = boxService;
	}

	// 모든 수거함 조회
	@GetMapping("admin/findAllBox")
	public List<Box> findAllBox() {
		return boxService.findAllBox();
	}

	// 수거함 이름으로 검색
	@GetMapping("admin/findBoxByName/{name}")
	public Box findBoxByName(@PathVariable(value = "name") String name) {
		return boxService.findBoxByName(name);
	}

	// 수거함 차단 및 해제
	@PatchMapping("admin/blockBox/{id}")
	public String blockBox(@PathVariable(value = "id") int id) {
		return boxService.blockBox(id);
	}

	// 사용 중인 수거함 강제 차단
	@PatchMapping("admin/superBlockBox/{id}")
	public String superBlockBox(@PathVariable(value = "id") int id) {
		return boxService.superBlockBox(id);
	}

	// 수거함 문열기
	// 테스트 불가 IOT 장비 연결 후 테스트
	@GetMapping("admin/boxOpen/{role}/{id}")
	public String boxOpen(@PathVariable(value = "role") String role, @PathVariable(value = "id") int id) {
		return boxService.boxControll("open", role, id);
	}

	// 수거함 문닫기
	// 테스트 불가 IOT 장비 연결 후 테스트
	@GetMapping("admin/boxClose/{role}/{id}")
	public String boxClose(@PathVariable(value = "role") String role, @PathVariable(value = "id") int id) {
		return boxService.boxControll("close", role, id);
	}

}
