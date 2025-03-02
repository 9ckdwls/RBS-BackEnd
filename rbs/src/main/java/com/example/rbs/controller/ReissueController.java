package com.example.rbs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rbs.jwt.JWTUtil;
import com.example.rbs.service.ReissueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {
	
	private final ReissueService reissueService;
	
	public ReissueController(ReissueService reissueService) {
		this.reissueService = reissueService;
	}
	
	// refresh 토큰으로 access 재발급 받기
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return reissueService.reissue(request, response);
	}
}
