package com.example.rbs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	//비밀번호 암호화
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		// 메인, 로그인, 회원가입 페이지는 아무나 접근 가능
		// 나머지 amdin/으로 시작하는 페이지는 ADMIN 권한이 있어야 접근 가능
		// 혹시 모를 나머지 요청도 ADMIN 권한이 있어야 접근 가능
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "join", "loginFail", "logout", "findId", "findPw").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().hasRole("ADMIN")
                );
        
        http
        	.formLogin((auth) -> auth
        			.loginProcessingUrl("/login") // 로그인 요청
        			.permitAll()
        			.successForwardUrl("/loginSuccess") // 로그인 성공
        			.failureForwardUrl("/loginFail") // 로그인 실패
        			
        	);
        
        http
        	.csrf((auth) -> auth.disable()); 
        
        http
        	.logout((auth) -> auth
        		.logoutSuccessUrl("/logoutSuccess") // 로그아웃 성공
        	);

        return http.build();
    }
}
