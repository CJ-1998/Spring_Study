package com.study.SpringStudy.common.filter;

import com.study.SpringStudy.common.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사 및 SecurityContext에 인증 정보 저장
        if (token != null && jwtProvider.validateToken(token)) {
            // 1. Redis 블랙리스트에 존재하는 토큰인지 확인
            String isLogout = redisTemplate.opsForValue().get(token);

            // 2. 로그아웃 상태가 아닐 때만 인증 처리
            if (ObjectUtils.isEmpty(isLogout)) {
                UUID userId = jwtProvider.getUserId(token);

                // SecurityContext에는 보통 유저를 식별할 수 있는 값을 넣습니다.
                // 여기서는 PK를 String으로 변환하거나, Custom UserDetails 객체를 만들어 넣는 것이 좋습니다.
                // 실제 상용 앱에서는 UserDetailsService를 통해 DB에서 유저를 조회 후 권한을 부여하는 것이 좋습니다.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 3. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
