package com.study.SpringStudy.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaAuditConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    // 람다식을 사용하여 AuditorAware 구현체 반환
    return () -> {
      // 실제 배포 시: Spring Security 등에서 현재 로그인한 유저 정보를 가져오는 로직
      // 예: return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());

      // 테스트 시: 임시로 특정 ID 반환
      return Optional.of("AdminUser");
    };
  }
}
