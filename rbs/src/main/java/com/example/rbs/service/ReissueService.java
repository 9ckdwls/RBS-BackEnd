package com.example.rbs.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.rbs.entity.Refresh;
import com.example.rbs.jwt.JWTUtil;
import com.example.rbs.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReissueService {

	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	public ReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}

	// refresh 토큰으로 access 재발급 받기
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		// 쿠키에서 refresh키로 refresh 가져오기
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		// 없으면 오류
		if (refresh == null) {
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		// 만료 됐으면 오류
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// refresh 내부 헤더 무결성 검사
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}
		
		// DB에 존재하는 refresh인지 확인
		boolean isExist = refreshRepository.existsByRefresh(refresh);
		if(!isExist) {
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String userId = jwtUtil.getUserId(refresh);
		String role = jwtUtil.getRole(refresh);

		// 새로운 refresh 발급
		String newAccess = jwtUtil.createJwt("access", userId, role, 10 * 60 * 1000L); // 10분
		String newRefresh = jwtUtil.createJwt("refresh", userId, role, 24 * 60 * 60 * 1000L); // 24시간
		
		// 기존 refresh DB에서 삭제
		refreshRepository.deleteByRefresh(refresh);
		// 새로운 refresh DB에 저장
		addRefresh(userId, refresh, 24 * 60 * 60 * 1000L);

		// 헤더에 access, 쿠키에 refresh
		response.setHeader("access", "Bearer " + newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 쿠키 생성
	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setHttpOnly(true);

		return cookie;
	}
	
	private void addRefresh(String userId, String refresh, Long expiredMs) {
		Date date = new Date(System.currentTimeMillis() + expiredMs);
		
		Refresh newRefresh = new Refresh();
		newRefresh.setUserId(userId);
		newRefresh.setRefresh(refresh);
		newRefresh.setExpiration(date);
		
		refreshRepository.save(newRefresh);
	}

}
