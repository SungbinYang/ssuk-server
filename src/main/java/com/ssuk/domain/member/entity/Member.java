package com.ssuk.domain.member.entity;

import com.ssuk.domain.member.type.MemberStatus;
import com.ssuk.domain.member.type.Role;
import com.ssuk.global.common.entity.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createdAt", column = @Column(name = "joined_at", nullable = false, updatable = false))
public class Member extends BaseDateTimeEntity {

    @Id
    @Comment("회원 테이블 PK")
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("회원 한국 이름")
    @Column(name = "member_korean_name", nullable = false)
    private String koreanName;

    @Comment("회원 영문 이름")
    @Column(name = "member_english_name", nullable = false)
    private String englishName;

    @Comment("회원 주민등록번호(앞 6자리 + 뒤 1자리)")
    @Column(name = "member_registration_number", nullable = false)
    private String registrationNumber;

    @Comment("회원 이메일")
    @Column(nullable = false, unique = true)
    private String email;

    @Comment("회원 비밀번호")
    @Column(nullable = false)
    private String password;

    @Comment("회원 상태")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private MemberStatus memberStatus;

    @Comment("회원 권한")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private Role role;

    @Comment("회원 테이블과 프로필 테이블의 FK")
    @JoinColumn(name = "profile_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @Builder
    public Member(String koreanName, String englishName, String registrationNumber, String email, String password) {
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.password = password;
        this.memberStatus = MemberStatus.ACTIVE;
        this.role = Role.MEMBER;
        this.profile = Profile.builder().build();
    }
}
