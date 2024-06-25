package com.ssuk.domain.member.part;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Builder
public class Address {

    @Comment("도로명 주소")
    private String district;

    @Comment("상세 주소")
    @Column(name = "address_detail")
    private String detail;

    @Comment("우편번호")
    private String zipcode;
}
