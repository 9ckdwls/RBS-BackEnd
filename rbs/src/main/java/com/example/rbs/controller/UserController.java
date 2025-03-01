package com.example.rbs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.JoinDTO;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.User;
import com.example.rbs.service.UserService;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// 일반 사용자 회원가입
	@PostMapping("join")
	public String join(@RequestBody JoinDTO joinDTO) {
		return userService.joinUser(joinDTO);
	}
	
	// 수거자 회원가입
	@PostMapping("joinRequest")
	public String joinRequest(@RequestBody JoinDTO joinDTO) {
		return userService.joinEmployee(joinDTO);
	}

	// 내정보 보기
	@GetMapping("myInfo")
	public User myInfo() {
		return userService.myInfo();
	}
	
	// 수거 및 분리 내역
	@GetMapping("myBoxLog")
	public List<BoxLog> myBoxLog() {
		return userService.myBoxLog();
	}
}
