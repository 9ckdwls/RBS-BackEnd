package com.example.rbs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.JoinDTO;
import com.example.rbs.service.UserService;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// 회원가입
	@PostMapping("join")
	public String join(@RequestBody JoinDTO joinDTO) {
		return userService.join(joinDTO);
	}
	
	@GetMapping("employee/test")
	public String test1() {
		System.out.println(userService.getId());
		System.out.println(userService.getRole());
		return "test1 ok";
	}
	
	@GetMapping("user/test")
	public String test2() {
		return "test2 ok";
	}
}
