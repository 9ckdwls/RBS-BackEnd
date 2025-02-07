package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.dto.FindUserDto;
import com.example.rbs.dto.JoinDto;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.User;
import com.example.rbs.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		if (userService.join(joinDto).equals("Fail")) {
			return "Fail";
		} else {
			return "Success";
		}
	}

	// 로그인 요청은 /login으로
	// form-data 타입으로 id는 key: username pw는 key: password
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
	@GetMapping("logoutSuccess")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "logoutSuccess";
	}

	// 사용자 id 찾기
	// key: name key: phoneNumber
	@PostMapping("findId")
	public String findId(@RequestBody FindUserDto findUserDto) {
		User user = userService.findId(findUserDto);
		if (user == null) {
			return "존재하지 않는 사용자입니다.";
		} else {
			return user.getId();
		}
	}

	// 사용자 pw 찾기
	// key: id key: name key: phoneNumber
	@PostMapping("findPw")
	public String findPw(@RequestBody FindUserDto findUserDto) {
		if(userService.findPw(findUserDto) == 1) {
			return "Success";
		} else {
			return "Fail";
		}
	}
	
	// 전체 사용자 조회
	@GetMapping("admin/findUserAll")
	public List<User> findUserAll() {
		return userService.findUserAll();
	}
	
	// id로 회원 검색하기
	@GetMapping("admin/findUser/{id}")
	public User findUserById(@PathVariable(value = "id") String id) {
		return userService.findUserByIdAndRole(id, "ROLE_USER");
	}
	
	// id로 수거자 검색
	@GetMapping("admin/findEmployee/{id}")
	public User findEmployee(@PathVariable(value = "id") String id) {
		return userService.findUserByIdAndRole(id, "ROLE_EMPLOYEE");
	}
	
	// userId로 수거함 로그 검색
	@GetMapping("admin/findBoxLogById/{userId}")
	public List<BoxLog> findBoxLogById(@PathVariable(value = "userId") String userId) {
		return userService.findByUserId(userId);
	}
	
	// 내정보 보기
	@GetMapping("admin/MyInfo/{id}")
	public User MyInfo(@PathVariable(value = "id") String id) {
		return userService.findUserByIdAndRole(id, "ROLE_ADMIN");
	}
	
	// 관리자 비밀번호 바꾸기
	@PatchMapping("admin/updatePw")
	public String updatePw(@RequestBody FindUserDto findUserDto) {
		if(userService.updatePw(findUserDto) == 1) {
			return "Success";
		} else {
			return "Fail";
		}
	}
}
