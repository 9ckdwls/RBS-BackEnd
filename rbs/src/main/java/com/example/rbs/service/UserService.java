package com.example.rbs.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.rbs.dto.JoinDTO;
import com.example.rbs.entity.User;
import com.example.rbs.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepositroy;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserService(UserRepository userRepositroy, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepositroy = userRepositroy;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	// 회원가입
	public String join(JoinDTO joinDTO) {

		if (userRepositroy.existsById(joinDTO.getId())) {
			return "Fail";
		} else {
			User user = new User();
			user.setId(joinDTO.getId());
			user.setPw(bCryptPasswordEncoder.encode(joinDTO.getPw()));
			user.setName(joinDTO.getName());
			user.setPhoneNumber(joinDTO.getPhoneNumber());
			user.setPoint(0);
			user.setDate(new Date());
			user.setRole(joinDTO.getRole());

			userRepositroy.save(user);

			return "Success";
		}
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
}
