package com.study.SpringStudy.domain.auth.service;

import com.study.SpringStudy.common.exception.ApiException;
import com.study.SpringStudy.common.exception.errorcode.UserErrorCode;
import com.study.SpringStudy.common.util.JwtProvider;
import com.study.SpringStudy.domain.user.entity.User;
import com.study.SpringStudy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    // 로그인 로직
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ApiException(UserErrorCode.INVALID_PASSWORD);
        }

        // 인증 성공 시 토큰 발급
        return jwtProvider.createToken(user.getId(), user.getRole().name());
    }

    public void logout(String token) {
        // 1. 토큰이 유효한지 1차 확인
        if (!jwtProvider.validateToken(token)) {
            throw new ApiException(UserErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰의 남은 만료 시간 계산
        long remainingTime = jwtProvider.getRemainingExpirationTime(token);

        // 3. 남은 시간동안만 Redis에 Blacklist로 저장 (Key: 토큰, Value: "logout")
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                    token,
                    "logout",
                    remainingTime,
                    TimeUnit.MILLISECONDS
            );
        }
    }
}