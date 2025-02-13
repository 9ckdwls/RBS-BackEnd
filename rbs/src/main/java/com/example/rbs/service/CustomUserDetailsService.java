package com.example.rbs.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.rbs.dto.CustomUserDetails;
import com.example.rbs.entity.User;
import com.example.rbs.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepositroy;

	public CustomUserDetailsService(UserRepository userRepositroy) {
		this.userRepositroy = userRepositroy;
	}

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

		Optional<User> user = userRepositroy.findById(id);
		if (user.isPresent()) {
			return new CustomUserDetails(user.get());
		}
		
		return null;

	}

}
