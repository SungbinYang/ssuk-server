package com.ssuk.domain.member.entity;

import com.ssuk.domain.member.part.Address;
import com.ssuk.global.common.entity.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseDateTimeEntity {

    @Id
    @Column(name = "profile_id")
    @Comment("프로필 테이블 PK")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("프로필 이미지")
    @Column(nullable = false)
    private String profileImage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "district", column = @Column(name = "home_district")),
            @AttributeOverride(name = "detail", column = @Column(name = "home_address_detail")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "home_zipcode"))
    })
    @Comment("프로필에 등록된 집 주소")
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "district", column = @Column(name = "company_district")),
            @AttributeOverride(name = "detail", column = @Column(name = "company_address_detail")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "company_zipcode"))
    })
    @Comment("프로필에 등록된 회사 주소")
    private Address companyAddress;

    @Comment("프로필에 등록된 직업")
    private String occupation;

    @Builder
    public Profile(String profileImage, Address homeAddress, Address companyAddress, String occupation) {
        this.profileImage = profileImage == null ? "default_image" : profileImage;
        this.homeAddress = homeAddress;
        this.companyAddress = companyAddress;
        this.occupation = occupation;
    }
}
