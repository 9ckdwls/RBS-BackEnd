package com.example.rbs.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.rbs.dto.FindUserDto;
import com.example.rbs.dto.JoinDto;
import com.example.rbs.entity.Box;
import com.example.rbs.entity.User;
import com.example.rbs.repository.UserRepository;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
public class UserService {

	private final UserRepository userRepositroy;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final DefaultMessageService messageService;
	private final PhoneVerificationService phoneVerificationService;

	@Value("${sms.from-number}")
	String FROM;

	public UserService(UserRepository userRepositroy, BCryptPasswordEncoder bCryptPasswordEncoder,
			PhoneVerificationService phoneVerificationService, @Value("${sms.api-key}") String API_KEY,
			@Value("${sms.api-secret-key}") String API_SECRET_KEY, @Value("${sms.domain}") String DOMAIN) {
		this.userRepositroy = userRepositroy;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.phoneVerificationService = phoneVerificationService;
		this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, DOMAIN);
	}

	// 회원가입 메소드
	public String join(JoinDto joinDTO) {

		// 전화번호 인증 코드 검증
		if (phoneVerificationService.verifyCode(joinDTO.getPhoneNumber(), joinDTO.getVerificationCode())
				.equals("Fail")) {
			System.out.println("1");
			return "phoneAuth code is not valid";
		}

		if (userRepositroy.existsByIdOrPhoneNumber(joinDTO.getId(), joinDTO.getPhoneNumber())) {
			return "Fail";
		}

		User user = new User();
		user.setId(joinDTO.getId());
		user.setPw(bCryptPasswordEncoder.encode(joinDTO.getPw()));
		user.setName(joinDTO.getName());
		user.setPhoneNumber(joinDTO.getPhoneNumber());
		user.setPoint(0);
		user.setDate(new Date());
		user.setRole("ROLE_ADMIN");
		user.setLocation1(joinDTO.getLocation1());
		user.setLocation2(joinDTO.getLocation2());

		userRepositroy.save(user);
		return "Success";
	}

	// 전화번호 인증 요청
	public String smsAuth(String to) {
		String verificationCode = phoneVerificationService.generateVerificationCode(to);
		Message message = new Message();
		// 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
		message.setFrom(FROM);
		message.setTo(to);
		// 인증 코드 발급 필요
		message.setText("인증코드: " + verificationCode);

		try {
			if (!isValidPhoneNumber(to)) {
				throw new IllegalArgumentException("phoneNumber is not valid");
			}
			messageService.sendOne(new SingleMessageSendingRequest(message));
			return "Success";
		} catch (IllegalArgumentException e) {
			return "phoneNuber is not valid: " + e.getMessage();
		} catch (Exception e) {
			// 다른 예외에 대한 일반적인 메시지 처리
			return "SMS error: " + e.getMessage();
		}
	}

	// 화재 신고
	public String smsFire(String to, Box box) {
		Message message = new Message();
		// 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
		message.setFrom(FROM);
		message.setTo(to);
		message.setText(box.getLocationAsText() + "수거함에서 화재 발생");

		try {
			messageService.sendOne(new SingleMessageSendingRequest(message));
			return "Success";
		} catch (IllegalArgumentException e) {
			return "phoneNuber is not valid: " + e.getMessage();
		} catch (Exception e) {
			// 다른 예외에 대한 일반적인 메시지 처리
			return "SMS error: " + e.getMessage();
		}
	}

	// 전화번호 검증
	private boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("^010\\d{8}$");
	}

	// 전화번호 인증 코드 검증
	public String verifyCode(String phone, String code) {
		return phoneVerificationService.verifyCode(phone, code);
	}

	// 사용자의 id
	public String getUserId() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	// 사용자의 권한
	public String getUserRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iter = authorities.iterator();
		GrantedAuthority auth = iter.next();
		return auth.getAuthority();
	}

	// 사용자 id 찾기
	public User findId(FindUserDto findUserDto) {
		Optional<User> user = userRepositroy.findByNameAndPhoneNumber(findUserDto.getName(),
				findUserDto.getPhoneNumber());
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	// 사용자 pw 찾기
	public int findPw(FindUserDto findUserDto) {
		Optional<User> user = userRepositroy.findByIdAndNameAndPhoneNumber(findUserDto.getId(), findUserDto.getName(),
				findUserDto.getPhoneNumber());
		if (user.isPresent()) {
			User myuser = user.get();
			myuser.setPw(bCryptPasswordEncoder.encode(findUserDto.getPw()));
			userRepositroy.save(myuser);
			return 1;
		}
		return 0;
	}

	// 전체 사용자 조회
	public List<User> findUserAll() {
		return userRepositroy.findUserAll();
	}

	// id와 권한으로 사용자 찾기
	public User findUserByIdAndRole(String id, String role) {
		Optional<User> user = userRepositroy.findByIdAndRole(id, role);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	// 내 비밀번호 확인
	public String checkPw(FindUserDto findUserDto) {
		if (bCryptPasswordEncoder.matches(findUserDto.getPw(), MyInfo().getPw())) {
			return "Success";
		} else {
			return "Fail";
		}
	}

	// 관리자 비밀번호 바꾸기
	public String updatePw(FindUserDto findUserDto) {
		Optional<User> user = userRepositroy.findById(getUserId());
		if (user.isPresent()) {
			User myuser = user.get();
			myuser.setPw(bCryptPasswordEncoder.encode(findUserDto.getPw()));
			userRepositroy.save(myuser);
			return "Success";
		}
		return "Fail";
	}

	// 내 정보 확인
	public User MyInfo() {
		Optional<User> user = userRepositroy.findById(getUserId());
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

	// 가입신청한 수거자 보기
	public List<User> showEmployeeRequest() {
		return userRepositroy.findByRole("ROLE_EMPLOYEE_REQUEST");
	}

	// 가입신청 수락하기
	public String permitJoin(String userId) {
		Optional<User> user = userRepositroy.findById(userId);
		if (user.isPresent()) {
			User myUser = user.get();
			myUser.setRole("ROLE_EMPLOYEE");
			userRepositroy.save(myUser);
			return "Success";
		}
		return "Fail";
	}

	// 가입신청 거절하기
	public String noJoin(String userId) {
		Optional<User> user = userRepositroy.findById(userId);
		if (user.isPresent()) {
			User myUser = user.get();
			myUser.setRole("ROLE_EMPLOYEE_NO");
			userRepositroy.save(myUser);
			return "Success";
		}
		return "Fail";
	}

	// 사용자 담당 구역 변경하기
	public String changeLocation(String userId, String location1, String location2) {
		Optional<User> user = userRepositroy.findById(userId);
		if (user.isPresent()) {
			User myUser = user.get();
			myUser.setLocation1(location1);
			myUser.setLocation2(location2);
			userRepositroy.save(myUser);
			return "Success";
		}
		return "Fail";
	}

	public void updatePonint(int value) {
		Optional<User> user = userRepositroy.findById(getUserId());
		if (user.isPresent()) {
			User myUser = user.get();
			myUser.setPoint(myUser.getPoint() + value);
			userRepositroy.save(myUser);
		} else {
			return;
		}
	}
}
