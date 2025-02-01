package com.example.rbs.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.rbs.service.UserService;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
}
