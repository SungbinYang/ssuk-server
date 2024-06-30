package com.ssuk.domain.member.model.request;

import com.ssuk.domain.member.entity.Member;
import com.ssuk.global.exception.custom.BusinessException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record MemberSignupSetupPasswordRequestDto(

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^\\d{4}[A-Z]$", message = "비밀번호는 앞의 4자리는 숫자로, 뒤 1자리는 영문으로 구성되어야 합니다.")
        String password,

        @NotBlank(message = "비밀번호(확인)를 입력해주세요.")
        @Pattern(regexp = "^\\d{4}[A-Z]$", message = "비밀번호는 앞의 4자리는 숫자로, 뒤 1자리는 영문으로 구성되어야 합니다.")
        String confirmPassword
) {
    public void validatePasswordAndConfirmPassword() {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("비밀번호와 비밀번호(확인)이 일치하지 않습니다.");
        }
    }

    public Member toMember(MemberSignupCollectMemberInfoRequestDto requestDto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .koreanName(requestDto.koreanName())
                .englishName(requestDto.englishName())
                .registrationNumber(requestDto.registrationNumber())
                .email(requestDto.email())
                .password(passwordEncoder.encode(password))
                .build();
    }
}
