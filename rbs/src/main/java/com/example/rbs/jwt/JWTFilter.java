package com.example.rbs.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.rbs.dto.CustomUserDetails;
import com.example.rbs.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {
	
	//accessToken이 소멸되었다면 401 응답 코드와 응답 메시지를 보고 재발급 받아야 함!!!!!!!!!!!!!!!!!!
	
	/* JWT를 검증하는 필터
	 * LoginFilter 바로 전에 등록한 필터로 유효한 JWT를 가진다면 통과, 유효하지 않은 JWT를 가지면 로그인 하도록 처리
	 */

	private final JWTUtil jwtUtil;

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		//요청 헤더가 access이어야 함
		String access = request.getHeader("access");

		//access이 없거나 Bearer로 시작하지 않으면 인증X
		if (access == null || !access.startsWith("Bearer ")) {
			System.out.println("accessToken null");
			filterChain.doFilter(request, response);
			return;
		}

		//Bearer를 제거한 JWT 가져오기
		String accessToken = access.split(" ")[1];

		// 토큰 소멸 시간 검증
		// accessToken이 소멸되었다면 이 401 응답 코드와 응답 메시지를 보고 재발급 받아야 함.
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			PrintWriter writer = response.getWriter();
			writer.print("accessToken is expired");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String category = jwtUtil.getCategory(accessToken);
		
		//토큰의 헤더값이 access인지 무결성 검사
		if(!category.equals("access")) {
			PrintWriter writer = response.getWriter();
			writer.print("invalid access token");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String userId = jwtUtil.getUserId(accessToken);
		String role = jwtUtil.getRole(accessToken);

		// User를 생성하여 값 set
		User user = new User();
		user.setId(userId);
		user.setRole(role);

		// UserDetails에 회원 정보 객체 담기
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		// 스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
				customUserDetails.getAuthorities());
		// 세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
