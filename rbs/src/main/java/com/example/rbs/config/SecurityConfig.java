package com.example.rbs.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.example.rbs.jwt.CustomLogoutFilter;
import com.example.rbs.jwt.JWTFilter;
import com.example.rbs.jwt.JWTUtil;
import com.example.rbs.jwt.LoginFilter;
import com.example.rbs.repository.RefreshRepository;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebMvc
public class SecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.cors((cors) -> cors
					.configurationSource(new CorsConfigurationSource() {
						@Override
						public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
							CorsConfiguration configuration = new CorsConfiguration();
							configuration.setAllowedOrigins(Collections.singletonList("*")); // 허용할 도메인
		                    configuration.setAllowedMethods(Collections.singletonList("*"));
		                    configuration.setAllowCredentials(true);
		                    configuration.setAllowedHeaders(Collections.singletonList("*"));
		                    configuration.setMaxAge(3600L);
		                    configuration.setExposedHeaders(Collections.singletonList("access"));
							return configuration;
						}
					}));
		
		http
			.csrf((auth) -> auth.disable());
		
		http
			.formLogin((auth) -> auth.disable());
		
		http
			.httpBasic((auth) -> auth.disable());
		
		http
			.authorizeHttpRequests((auth) -> auth
					.requestMatchers("/login", "/join", "/joinRequest", "/reissue").permitAll() // 회원가입, 로그인만 모두 허용
					.requestMatchers("/user/**").hasRole("USER")
					.requestMatchers("/employee/**").hasRole("EMPLOYEE")
					.anyRequest().authenticated());
		
		// JWTFilter는 로그인 전에 작동해서 JWT가 있다면 인증에 성공, 만료되었다면 재발급 or 로그인
		http
			.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
		
		// LoginFilter는 기존 UsernamePasswordAuthenticationFilter에 위치해서 우리가 커스텀한 로직으로 작동하도록
		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
		
		// CustomLogoutFilter는 기존 LogoutFilter 전에 위치
		http
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
		
		// JWT 사용 시 세션은 STATELESS로 관리
		http
			.sessionManagement((session) -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
			
		return http.build();
	}

}
