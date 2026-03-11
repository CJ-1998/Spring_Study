package com.study.SpringStudy.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaAuditConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    // 람다식을 사용하여 AuditorAware 구현체 반환
    return () -> {
      // 1. SecurityContext에서 현재 인증 정보(Authentication)를 가져옵니다.
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // 2. 인증 정보가 없거나, 로그인하지 않은 익명 사용자(anonymousUser)인 경우
      if (authentication == null || !authentication.isAuthenticated() ||
          authentication.getPrincipal().equals("anonymousUser")) {
        // 회원가입 등 로그인하지 않은 상태에서의 DB 저장은 "SYSTEM"으로 기록합니다.
        return Optional.of("SYSTEM");
      }

      // 3. 필터에서 우리가 UUID를 principal에 넣었으므로, 꺼내서 String으로 변환합니다.
      Object principal = authentication.getPrincipal();
      if (principal instanceof UUID) {
        return Optional.of(principal.toString());
      }

      // 만약 다른 타입이 들어온다면 기본 처리를 위해 toString() 반환
      return Optional.of(principal.toString());
    };
  }
}
