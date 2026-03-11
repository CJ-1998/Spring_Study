package com.study.SpringStudy.domain.auth.controller;

import com.study.SpringStudy.common.exception.errorcode.CommonErrorCode;
import com.study.SpringStudy.common.response.ApiResponse;
import com.study.SpringStudy.domain.auth.dto.request.LoginRequest;
import com.study.SpringStudy.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인 & 회원가입 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. 서비스 로직 호출 (이메일, 평문 비밀번호 전달)
        String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. 작성해두신 공통 응답 객체(ApiResponse)로 JWT 토큰 반환
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {

        // 1. 헤더에서 토큰 추출 (Bearer 제외)
        String token = resolveToken(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(CommonErrorCode.INVALID_INPUT_VALUE));
        }

        // 2. 서비스 호출하여 블랙리스트 등록
        authService.logout(token);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 로그아웃 되었습니다."));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}