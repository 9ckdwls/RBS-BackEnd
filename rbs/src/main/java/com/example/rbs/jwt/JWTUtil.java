package com.example.rbs.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {
	/* JWT를 발급하는 클래스
	 * JWT는 0.12.3
	 * JWT 생성, JWT 내부의 카테고리(액세스, 리프레시), userId, 권한, 만료시간을 알 수 있다.
	 */

	
	private SecretKey secretKey;

	//@Value로 application.properties에 정의된 암호문을 가져온다.
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String userId, String role, Long expiredMs) {
        return Jwts.builder()
        		.claim("category", category)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 발행시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 소멸시간
                .signWith(secretKey)
                .compact();
    }
}
