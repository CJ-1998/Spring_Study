package com.study.SpringStudy.domain.auth.service;

import com.study.SpringStudy.common.exception.ApiException;
import com.study.SpringStudy.common.exception.errorcode.UserErrorCode;
import com.study.SpringStudy.common.util.JwtProvider;
import com.study.SpringStudy.domain.user.entity.User;
import com.study.SpringStudy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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
}