package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.dto.FindUserDto;
import com.example.rbs.dto.JoinDto;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.User;
import com.example.rbs.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Locked.Read;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// 회원가입 요청
	// JoinDto : id, pw, name, phoneNuber, verificationCode
	@PostMapping("join")
	public String join(@RequestBody JoinDto joinDto) {
		return userService.join(joinDto);
	}

	// 전화번호 인증 요청
	// to: 전화번호
	@PostMapping("/send-one/{to}")
	public String sendOne(@PathVariable(value = "to") String to) {
		return userService.smsAuth(to);
	}

	// 전화번호 인증 코드 검증
	@PostMapping("/verify-code")
	public String verifyCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
		return userService.verifyCode(phone, code);
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
	// FindUserDto: name, phoneNumber
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
	// FindUserDto: id, name, phoneNumber
	@PostMapping("findPw")
	public String findPw(@RequestBody FindUserDto findUserDto) {
		if (userService.findPw(findUserDto) == 1) {
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
	@GetMapping("admin/findUser/{useId}")
	public User findUserById(@PathVariable(value = "useId") String useId) {
		return userService.findUserByIdAndRole(useId, "ROLE_USER");
	}

	// id로 수거자 검색
	@GetMapping("admin/findEmployee/{useId}")
	public User findEmployee(@PathVariable(value = "useId") String useId) {
		return userService.findUserByIdAndRole(useId, "ROLE_EMPLOYEE");
	}

	// 내정보 보기
	@GetMapping("admin/MyInfo")
	public User MyInfo() {
		return userService.MyInfo();
	}

	// 내 비밀번호 확인
	// FindUserDto: pw
	@PostMapping("admin/checkPw")
	public String checkPw(@RequestBody FindUserDto findUserDto) {
		return userService.checkPw(findUserDto);
	}

	// 관리자 비밀번호 바꾸기
	// FindUserDto: pw
	@PatchMapping("admin/updatePw")
	public String updatePw(@RequestBody FindUserDto findUserDto) {
		return userService.updatePw(findUserDto);
	}

	// 가입신청한 수거자 보기
	@GetMapping("admin/showEmployeeRequest")
	public List<User> showEmployeeRequest() {
		return userService.showEmployeeRequest();
	}

	// 가입신청 수락하기
	@PatchMapping("admin/permitJoin/{userId}")
	public String permitJoin(@PathVariable(value = "userId") String userId) {
		return userService.permitJoin(userId);
	}

	// 가입신청 거절하기
	@PatchMapping("admin/noJoin/{userId}")
	public String noJoin(@PathVariable(value = "userId") String userId) {
		return userService.noJoin(userId);
	}

	// 사용자 담당 구역 변경하기
	// location(화성시, 아산시 등)
	@PatchMapping("admin/changeLocation/{userId}/{location}")
	public String changeLocation(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "location") String location) {
		return userService.changeLocation(userId, location);
	}
}
