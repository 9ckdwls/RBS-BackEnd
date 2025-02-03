package com.example.rbs.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.CodeService;

@RestController
public class CodeController {
	
	private final CodeService codeService;
	
	public CodeController(CodeService codeService) {
		this.codeService = codeService;
	}
	
	// 수거자 가입 코드 찾기
	// 가입 코드는 오류 시 -1을 반환한다.
	@GetMapping("admin/findCode")
	public int findCode() {
		return codeService.findCode();
	}
	
	// 수거자 가입 코드 변경
	@PatchMapping("admin/updateCode")
	public String updateCode(@RequestBody Map<String, Integer> request) {
		System.out.println("saljdlkas");
		if(codeService.updateCode(request.get("code")) == 1) {
			return "Success";
		} else {
			return "Fail";
		}
	}

}
