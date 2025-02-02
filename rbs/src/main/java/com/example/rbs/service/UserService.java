package com.example.rbs.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.rbs.dto.FindUserDto;
import com.example.rbs.dto.JoinDto;
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

	// 회원가입 메소드
	public String join(JoinDto joinDto) {

		if (userRepositroy.existsById(joinDto.getId())) {
			return "Fail";
		}

		User user = new User();
		user.setId(joinDto.getId());
		user.setPw(bCryptPasswordEncoder.encode(joinDto.getPw()));
		user.setName(joinDto.getName());
		user.setPhoneNumber(joinDto.getPhoneNumber());
		user.setPoint(0);
		user.setDate(new Date());
		user.setRole("ROLE_ADMIN");

		userRepositroy.save(user);
		return "Success";
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
		Optional<User> user = userRepositroy.findByNameAndPhoneNumber(findUserDto.getName(), findUserDto.getPhoneNumber());
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	// 사용자 pw 찾기
	public int findPw(FindUserDto findUserDto) {
		Optional<User> user = userRepositroy.findByIdAndNameAndPhoneNumber(findUserDto.getId(), findUserDto.getName(), findUserDto.getPhoneNumber());
		if (user.isPresent()) {
			User myuser = user.get();
			myuser.setPw(bCryptPasswordEncoder.encode(findUserDto.getPw()));
			userRepositroy.save(myuser);
			return 1;
		}
		return 0;
	}
}
