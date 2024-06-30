package com.ssuk.domain.member.model.request;

import com.ssuk.domain.member.entity.MemberBaseInfo;
import com.ssuk.global.exception.custom.BusinessException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignupCollectMemberInfoRequestDto(

        @NotBlank(message = "회원님의 이름을 입력해주세요.")
        @Size(min = 2, message = "이름은 최소 2글자 이상이어야 합니다.")
        @Pattern(regexp = "^[가-힣]+$", message = "한글 이름은 한글 이외의 다른 문자가 들어갈 수 없습니다.")
        String koreanName,

        @NotBlank(message = "회원님의 영문 이름을 입력해주세요.")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "영문 이름은 영어 이외의 다른 문자가 들어갈 수 없습니다.")
        String englishName,

        @NotBlank(message = "주민등록번호는 반드시 입력해주셔야 합니다.")
        @Size(min = 7, max = 7, message = "주민등록번호는 7자리여야 합니다.")
        @Pattern(regexp = "^\\d{6}[1-4]$",
                message = "주민등록번호 형식에 맞지 않은 값입니다.")
        String registrationNumber,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞춰서 입력해주세요.")
        String email
) {
    /**
     * 주민등록번호 validation method
     *
     */
    public void validateRegistrationNumber() {
        int year = Integer.parseInt(registrationNumber.substring(0, 2));
        int month = Integer.parseInt(registrationNumber.substring(2, 4));
        int day = Integer.parseInt(registrationNumber.substring(4, 6));
        int centuryIndicator = Integer.parseInt(registrationNumber.substring(6));

        switch (centuryIndicator) {
            case 1, 2 -> year += 1900;
            case 3, 4 -> year += 2000;
            default -> throw new BusinessException("유효하지 않은 성별 및 세기 구분자입니다.");
        }

        if (month < 1 || month > 12) {
            throw new BusinessException("유효하지 않은 월 입니다.");
        }

        if (!isValidDay(year, month, day)) {
            throw new BusinessException("유효하지 않은 일입니다.");
        }
    }

    /**
     * 날짜 유효성 검사
     * @param year
     * @param month
     * @param day
     * @return
     */
    private boolean isValidDay(int year, int month, int day) {
        if (day < 1) {
            return false;
        }

        switch (month) {
            case 2:
                if (isLeapYear(year)) {
                    return day <= 29;
                } else {
                    return day <= 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return day <= 30;
            default:
                return day <= 31;
        }
    }

    /**
     * 윤년 검사
     * @param year
     * @return
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
