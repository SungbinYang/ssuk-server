package com.ssuk.domain.member.entity;

import com.ssuk.domain.member.model.request.MemberSignupCollectMemberInfoRequestDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "memberBaseInfo", timeToLive = 600)
public class MemberBaseInfo {

    @Id
    private String id;

    @Indexed
    private MemberSignupCollectMemberInfoRequestDto memberBaseInfo;

    @TimeToLive
    private Long expiredTime;
}
