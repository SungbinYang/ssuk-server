package com.ssuk.domain.member.type;

import com.ssuk.global.common.type.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role implements EnumType {
    MEMBER("일반 회원 권한"),
    ADMIN("관리자 권한");

    private final String description;
}
