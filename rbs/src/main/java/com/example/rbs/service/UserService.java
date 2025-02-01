package com.example.rbs.service;

import org.springframework.stereotype.Service;
import com.example.rbs.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepositroy;
	
	public UserService(UserRepository userRepositroy) {
		this.userRepositroy = userRepositroy;
	}
}
