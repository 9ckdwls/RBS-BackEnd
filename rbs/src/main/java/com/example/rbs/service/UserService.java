package com.example.rbs.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

@Service
public class UserService {

	private final UserRepository userRepositroy;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final BoxLogRepository boxLogRepository;

	public UserService(UserRepository userRepositroy, BCryptPasswordEncoder bCryptPasswordEncoder,
			BoxLogRepository boxLogRepository) {
		this.userRepositroy = userRepositroy;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.boxLogRepository = boxLogRepository;
	}

	// 회원가입 또는 가입신청
	public String join(JoinDTO joinDTO, String role) {

		if (userRepositroy.existsByIdAndPhoneNumber(joinDTO.getId(), joinDTO.getPhoneNumber())) {
			return "Fail";
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

	// 수거 및 분리 내역
	public List<BoxLog> myBoxLog() {
		return boxLogRepository.findByUserId(getId());
	}
}
