package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.CloseBoxResponseDTO;
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
	@GetMapping("boxOpen/{boxId}/{number}")
	public Object boxOpen(@PathVariable(value = "boxId") int boxId, @PathVariable(value = "number") int number) {
		Object response = boxService.boxControll("boxOpen", boxId, number);
		return response;
	}

	// 수거함 문닫기
	// 테스트 불가 IOT 장비 연결 후 테스트
	@GetMapping("boxClose/{boxId}/{number}")
	public int boxClose(@PathVariable(value = "boxId") int boxId, @PathVariable(value = "number") int number) {
		int response = boxService.boxControll("boxClose", boxId, number);
		return response;
	}

	// 수거함 사용 끝
	@GetMapping("boxEnd/{boxId}")
	public int boxEnd(@PathVariable(value = "boxId") int boxId) {
		return boxService.boxEnd(boxId);
	}

	// 익명 사용자 수거함 이용
	@PostMapping("boxUse")
	public String boxUse(@RequestBody CloseBoxResponseDTO dto) {
		System.out.println(dto);
		return boxService.boxUse(dto);
	}
}
