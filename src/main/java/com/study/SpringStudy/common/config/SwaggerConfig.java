package com.study.SpringStudy.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 1. 스프링 설정 클래스임을 명시
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // 💡 1. Security 스키마 이름 정의
    String jwtSchemeName = "jwtAuth";

    // 💡 2. 모든 API 요청 시 해당 스키마(JWT)를 요구하도록 전역 설정
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

    // 💡 3. Security 스키마 상세 설정 (Bearer 토큰 방식)
    Components components = new Components()
        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP) // HTTP 방식
            .scheme("bearer") // Bearer 타입 지정
            .bearerFormat("JWT")); // 포맷은 JWT

    return new OpenAPI()
        .info(apiInfo())
        .addSecurityItem(securityRequirement) // 전역 시큐리티 요구사항 추가
        .components(components); // 컴포넌트에 스키마 추가
  }

  private Info apiInfo() {
    return new Info()
        .title("SpringStudy API") // 2. API 제목
        .description("SpringStudy API") // 3. API 설명
        .version("1.0.0"); // 4. API 버전
  }
}
