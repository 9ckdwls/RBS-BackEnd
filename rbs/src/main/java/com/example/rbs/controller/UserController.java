package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.dto.JoinDto;
import com.example.rbs.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// 회원가입 요청
	// Fail: 요청 다시 Success: 메인 페이지로 이동
	@PostMapping("join")
	public String join(@RequestBody JoinDto joinDto) {
		if(userService.join(joinDto).equals("Fail")) {
			return "Fail";
		} else {
			return "Success";
		}
	}
	
	// 로그인 요청은 /login으로
	// Success: 로그인 성공 Fail: 로그인 실패
	
	// 프론트에서 접근 X
	@PostMapping("loginSuccess")
	public String loginSuccess() {
		return "Success";
	}
	
	// 프론트에서 접근 X
	@PostMapping("loginFail")
	public String loginFail() {
		return "Fail";
	}
	
	// 프론트에서 접근 X
	// 로그아웃 성공 return용
	@PostMapping("logoutSuccess")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "logoutSuccess";
	}

}
