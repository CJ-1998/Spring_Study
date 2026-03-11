package com.study.SpringStudy.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {
    @NotBlank(message = "Refresh Token은 필수 입력값입니다.")
    private String refreshToken;
}
