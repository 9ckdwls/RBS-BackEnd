package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.dto.IOTResponseDTO;
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
	@GetMapping("admin/findBoxByName/{boxName}")
	public Box findBoxByName(@PathVariable(value = "boxName") String boxName) {
		return boxService.findBoxByName(boxName);
	}

	// 수거함 차단 및 해제
	@PatchMapping("admin/blockBox/{boxId}")
	public String blockBox(@PathVariable(value = "boxId") int boxId) {
		return boxService.blockBox(boxId);
	}

	// 사용 중인 수거함 강제 차단
	@PatchMapping("admin/superBlockBox/{boxId}")
	public String superBlockBox(@PathVariable(value = "boxId") int boxId) {
		return boxService.superBlockBox(boxId);
	}

	// 수거함 문열기
	@GetMapping("admin/boxOpen/{boxId}/{number}")
	public Object boxOpen(@PathVariable(value = "boxId") int boxId,
			@PathVariable(value = "number") int number) {
		return boxService.boxControll("boxOpen", boxId, number);
	}

	// 수거함 문닫기
	@GetMapping("admin/boxClose/{boxId}/{number}")
	public Object boxClose(@PathVariable(value = "boxId") int boxId,
			@PathVariable(value = "number") int number) {
		return boxService.boxControll("boxClose",boxId, number);
	}

}
