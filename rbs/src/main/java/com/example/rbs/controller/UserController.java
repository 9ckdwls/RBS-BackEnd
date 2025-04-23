package com.example.rbs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.dto.JoinDTO;
import com.example.rbs.entity.User;
import com.example.rbs.service.UserService;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// 일반 사용자 회원가입
	// JoinDTO: id, pw, name, phoneNumber, verificationCode
	@PostMapping("join")
	public String join(@RequestBody JoinDTO joinDTO) {
		return userService.joinUser(joinDTO);
	}
	
	// 수거자 회원가입
	// JoinDTO: id, pw, name, phoneNumber, verificationCode
	@PostMapping("joinRequest")
	public String joinRequest(@RequestBody JoinDTO joinDTO) {
		return userService.joinEmployee(joinDTO);
	}
	
	// 전화번호 인증 요청
	// to는 전화번호
	@PostMapping("/send-one/{to}")
	public String sendOne(@PathVariable(value = "to") String to) {
		return userService.smsAuth(to);
	}
	
	// 전화번호 인증 코드 검증
	@PostMapping("/verify-code")
	public String verifyCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
	    return userService.verifyCode(phone, code);
	}
	
	// 로그인 요청은 /logindmfh
	// form-data 타입으로 id는 key: username pw는 key: password
	// Success: 로그인 성공 Fail: 로그인 실패
	
	// 로그아웃 요청은 /logout
	// 해당 refresh 및 유효기간 만료된 refresh 삭제

	// 토큰 재발급은 ReissueController 참조
	
	// 내정보 보기
	@GetMapping("myInfo")
	public User myInfo() {
		return userService.myInfo();
	}
}
