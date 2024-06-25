package com.ssuk.domain.member.type;

import com.ssuk.global.common.type.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus implements EnumType {
    DORMANT("휴면 회원"),
    ACTIVE("활성화 회원"),
    WITHDRAW("탈퇴 회원"),
    BAN("차단 회원");

    private final String description;
}
