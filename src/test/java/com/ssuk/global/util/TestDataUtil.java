package com.ssuk.global.util;

import com.ssuk.domain.member.entity.Member;
import com.ssuk.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestComponent
public class TestDataUtil {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createTestMember() {
        Member member = Member.builder()
                .koreanName("테스터")
                .englishName("TESTER")
                .registrationNumber("0011113")
                .email("test@email.com")
                .password(passwordEncoder.encode("1234A"))
                .build();

        this.memberRepository.save(member);
    }
}
