package com.example.rbs.jwt;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.rbs.dto.CustomUserDetails;
import com.example.rbs.entity.Refresh;
import com.example.rbs.repository.RefreshRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	
	/* UsernamePasswordAuthenticationFilter를 커스텀한 LoginFilter
	 * successfulAuthentication 메서드 내부 코드를 변경하여 access, refresh JWT 발급
	 * 추가로 여기서 기한이 지난 refresh DB에서 삭제
	 * 발급한 refresh DB에 저장
	 * 헤더에 access, 쿠키에 refresh 응답
	 */
	
	/* 로그인 요청 시 최초 스프링시큐리티 필터의 UsernamePasswordAuthenticationFilter 작동
	 * 여기서 id, pw만 뽑아서 UsernamePasswordAuthenticationToken(dto)에 넣어서 
	 * DB에 있는 user 데이터와 비교하는 authenticationManager로 넘김
	 */
	
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		// 클라이언트의 요청에서 id, pw 가져오기
		String id = obtainUsername(request);
		String pw = obtainPassword(request);
		
		// id, pw 검증을 위한 dto(UsernamePasswordAuthenticationToken)
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(id, pw, null);
		
		return authenticationManager.authenticate(authToken);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) {
		
		//CustomUserDetails은 DB에서 user 데이터를 가져와서 저장해둔 dto
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		
		String userId = customUserDetails.getUsername(); // user의 id 반환
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        
        //JWT 생성
        String access = jwtUtil.createJwt("access", userId, role, 10*60*1000L); //10분
        String refresh = jwtUtil.createJwt("refresh", userId, role, 24*60*60*1000L); //24시간
        
        //원래는 스케줄을 통해 기간이 지난 refresh 토큰을 제거 해야하지만 누군가 로그인 성공 했을 때 여기서 제거하도록 구성함
        refreshRepository.deleteByExpirationBefore(new Date());
        
        //발급한 refresh를 DB에 저장하는 메서드
        addRefresh(userId, refresh, 1000L);
        
        //헤더에 access, 쿠키에 refresh
        response.setHeader("access", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
	}
	
	//로그인 실패 시 401 응답
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		response.setStatus(401);
	}
	
	//쿠키 생성하는 메서드
	private Cookie createCookie(String key, String value) {
		
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);
		
		return cookie;
	}
	
	//발급한 refresh를 DB에 저장하는 메서드
	private void addRefresh(String userId, String refresh, Long expiredMs) {
		Date date = new Date(System.currentTimeMillis() + expiredMs);
		
		Refresh newRefresh = new Refresh();
		newRefresh.setUserId(userId);
		newRefresh.setRefresh(refresh);
		newRefresh.setExpiration(date);
		
		refreshRepository.save(newRefresh);
	}

}
