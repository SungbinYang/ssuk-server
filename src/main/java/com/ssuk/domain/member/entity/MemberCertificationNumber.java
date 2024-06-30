package com.ssuk.domain.member.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "certificationNumber", timeToLive = 300)
public class MemberCertificationNumber {

    @Id
    private String id;

    @Indexed
    private String certificationNumber;

    @TimeToLive
    private Long expiredTime;
}
