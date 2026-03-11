package com.study.SpringStudy.domain.auth.service;

import com.study.SpringStudy.common.exception.ApiException;
import com.study.SpringStudy.common.exception.errorcode.AuthErrorCode;
import com.study.SpringStudy.common.exception.errorcode.UserErrorCode;
import com.study.SpringStudy.common.util.JwtProvider;
import com.study.SpringStudy.domain.auth.dto.request.SignupRequest;
import com.study.SpringStudy.domain.auth.dto.response.LoginResponse;
import com.study.SpringStudy.domain.user.entity.Role;
import com.study.SpringStudy.domain.user.entity.User;
import com.study.SpringStudy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    public static final String REFRESH_TOKEN = "RefreshToken:";
    public static final String LOGOUT = "logout";

    // 회원 가입 로직 추가
    @Transactional // DB 쓰기 작업이므로 트랜잭션 보장
    public void signup(SignupRequest request) {
        // 1. 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(UserErrorCode.EXIST_EMAIL);
        }

        // 2. 비밀번호 단방향 암호화 (Bcrypt)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 엔티티 생성
        User newUser = User.createUser(request.getEmail(), encodedPassword);

        // 4. DB에 저장
        userRepository.save(newUser);
    }

    // 로그인 로직
    public LoginResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ApiException(UserErrorCode.INVALID_PASSWORD);
        }

        // 토큰 2개 발급
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장 (Key: "RefreshToken:{userId}", Value: refreshToken)
        setInRedis(REFRESH_TOKEN + user.getId(), refreshToken, jwtProvider.getRefreshExpiration());

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(String token) {
        // 1. 토큰이 유효한지 1차 확인
        if (!jwtProvider.validateToken(token)) {
            throw new ApiException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        // 2. 토큰의 남은 만료 시간 계산
        long remainingTime = jwtProvider.getRemainingExpirationTime(token);

        // 3. 남은 시간동안만 Redis에 Blacklist로 저장 (Key: 토큰, Value: "logout")
        if (remainingTime > 0) {
            setInRedis(token, LOGOUT, remainingTime);
        }
    }

    // 💡 2. 토큰 재발급(Refresh) 로직 추가
    public LoginResponse refresh(String refreshToken) {
        // 1. 넘어온 Refresh Token 자체의 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new ApiException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 토큰에서 유저 PK(UUID) 추출
        UUID userId = jwtProvider.getUserId(refreshToken);

        validateRefreshToken(refreshToken, userId);

        String newAccessToken = getNewAccessToken(userId);

        // (선택) Refresh Token Rotation(RTR) 기법: Refresh Token도 새로 발급해서 갱신할 수 있지만,
        // 여기서는 가장 기본적인 형태인 Access Token만 새로 갱신하여 반환합니다.
        return new LoginResponse(newAccessToken, refreshToken);
    }

    private void validateRefreshToken(String refreshToken, UUID userId) {
        // 3. Redis에 저장된 진짜 Refresh Token 조회
        String savedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN + userId);

        // 4. Redis에 없거나, 탈취범이 보낸 다른 토큰이라면 차단
        if (ObjectUtils.isEmpty(savedToken) || !savedToken.equals(refreshToken)) {
            throw new ApiException(AuthErrorCode.TIMEOUT_REFRESH_TOKEN);
        }
    }

    private String getNewAccessToken(UUID userId) {
        // 5. 검증 완료 -> 유저 정보 조회 후 새로운 Access Token 발급
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(UserErrorCode.USER_NOT_FOUND));

        return jwtProvider.createAccessToken(user.getId(), user.getRole().name());
    }

    private void setInRedis(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(
                key,
                value,
                timeout,
                TimeUnit.MILLISECONDS
        );
    }
}
