package com.ssuk.domain.member.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberSignupVerifyCodeRequestDto(

        @NotBlank(message = "인증코드는 공란일 수 없습니다.")
        @Size(min = 6, max = 6, message = "인증코드는 6자리 숫자입니다.")
        String certificationNumber
) {
}
