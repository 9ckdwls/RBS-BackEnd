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
import com.example.rbs.dto.JoinDTO;
import com.example.rbs.entity.BoxLog;
import com.example.rbs.entity.User;
import com.example.rbs.repository.BoxLogRepository;
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
	private final BoxLogRepository boxLogRepository;
	private final DefaultMessageService messageService;
	private final PhoneVerificationService phoneVerificationService;
	
	@Value("${sms.from-number}") String FROM;

	public UserService(UserRepository userRepositroy, BCryptPasswordEncoder bCryptPasswordEncoder,
			BoxLogRepository boxLogRepository, PhoneVerificationService phoneVerificationService,
			@Value("${sms.api-key}") String API_KEY,
			@Value("${sms.api-secret-key}") String API_SECRET_KEY,
			@Value("${sms.domain}") String DOMAIN) {
		this.userRepositroy = userRepositroy;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.boxLogRepository = boxLogRepository;
		this.phoneVerificationService = phoneVerificationService;
		this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, DOMAIN);
	}

	// 회원가입 또는 가입신청
	public String join(JoinDTO joinDTO, String role) {
		
		// 전화번호 인증 코드 검증
	    if (phoneVerificationService.verifyCode(joinDTO.getPhoneNumber(), joinDTO.getVerificationCode()).equals("Fail")) {
	    	System.out.println("1");
	        return "phoneAuth code is not valid";
	    }
	    	System.out.println("2");
		if (userRepositroy.existsByIdOrPhoneNumber(joinDTO.getId(), joinDTO.getPhoneNumber())) {
			return "Existing ID or phone number";
		} else {
			User user = new User();
			user.setId(joinDTO.getId());
			user.setPw(bCryptPasswordEncoder.encode(joinDTO.getPw()));
			user.setName(joinDTO.getName());
			user.setPhoneNumber(joinDTO.getPhoneNumber());
			user.setPoint(0);
			user.setDate(new Date());
			user.setRole(role);

			userRepositroy.save(user);

			return "Success";
		}
	}

	// 일반 사용자 회원가입
	public String joinUser(JoinDTO joinDTO) {
		return join(joinDTO, "ROLE_USER");
	}
	
	// 수거자 회원가입
	public String joinEmployee(JoinDTO joinDTO) {
		return join(joinDTO, "ROLE_EMPLOYEE_REQUEST");
	}

	// 내 id 가져오기
	public String getId() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	// 내 권한 가져오기
	public String getRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iter = authorities.iterator();
		GrantedAuthority auth = iter.next();
		return auth.getAuthority();
	}

	// 내 정보 확인
	public User myInfo() {
		Optional<User> user = userRepositroy.findById(getId());
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

	// 전화번호 인증 요청
	public SingleMessageSentResponse smsAuth(String to) {
		String verificationCode = phoneVerificationService.generateVerificationCode(to);
		Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom(FROM);
        message.setTo(to);
        // 인증 코드 발급 필요
        message.setText("인증코드: " + verificationCode);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
	}

	// 전화번호 인증 코드 검증
	public String verifyCode(String phone, String code) {
		return phoneVerificationService.verifyCode(phone, code);
	}
}
