package com.study.SpringStudy.common.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // String 시크릿키를 암호화 알고리즘에 맞는 SecretKey 객체로 변환
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성 (권한 정보 포함, 짧은 수명)
    public String createAccessToken(UUID userId, String role) {
        return buildToken(userId, role, accessExpiration);
    }

    // Refresh Token 생성 (권한 정보 불필요, 긴 수명)
    public String createRefreshToken(UUID userId) {
        return buildToken(userId, null, refreshExpiration);
    }

    // 중복 코드 제거를 위한 private 메서드
    private String buildToken(UUID userId, String role, long expiration) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key);

        if (role != null) {
            builder.claim("role", role);
        }
        return builder.compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 만료되었거나 변조된 경우
            return false;
        }
    }

    // 💡 토큰에서 이메일이 아닌 userId(PK)를 추출하여 UUID 타입으로 반환
    public UUID getUserId(String token) {
        String subject = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
        return UUID.fromString(subject);
    }

    // 토큰의 남은 유효 시간을 Milliseconds 단위로 반환
    public long getRemainingExpirationTime(String token) {
        Date expiration = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    // Refresh Token의 만료 시간을 외부(Service)에서 쓸 수 있도록 Getter 제공
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}