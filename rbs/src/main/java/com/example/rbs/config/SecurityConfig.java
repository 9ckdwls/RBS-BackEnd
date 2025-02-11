package com.example.rbs.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		
		http.addFilterBefore(new IpFilter(), UsernamePasswordAuthenticationFilter.class);

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
        		.logoutSuccessUrl("/logoutSuccess").permitAll() // 로그아웃 성공
        	);
        http
        	.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);
                return configuration;
            }
        })));
        http
        	.exceptionHandling(configurer ->
        		configurer
        			.accessDeniedHandler((request, response, accessDeniedException) -> {
        				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        			})
        			.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        	);

        return http.build();
    }
	
	// IP 체크를 위한 필터 정의
    public class IpFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String clientIp = request.getRemoteAddr();

            // 앱 서버 IP만 허용
            if (request.getRequestURI().startsWith("/userFrontServer")) {
                if (!"사용자 앱 서버의 IP".equals(clientIp)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Invalid IP Address");
                    return;
                }
            }
            
            if (request.getRequestURI().startsWith("/employeeFrontServer")) {
                if (!"수거자 앱 서버의 IP".equals(clientIp)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Invalid IP Address");
                    return;
                }
            }

            // 필터 체인 계속 진행
            filterChain.doFilter(request, response);
        }
    }
}
