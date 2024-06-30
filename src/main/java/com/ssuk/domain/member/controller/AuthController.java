package com.ssuk.domain.member.controller;

import com.ssuk.App;
import com.ssuk.domain.member.model.request.MemberSignupCollectMemberInfoRequestDto;
import com.ssuk.domain.member.model.request.MemberSignupVerifyCodeRequestDto;
import com.ssuk.domain.member.service.AuthService;
import com.ssuk.global.common.resource.ObjectResource;
import com.ssuk.global.response.success.SuccessCommonApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth", produces = MediaTypes.HAL_JSON_VALUE)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/collect-member-info")
    public ResponseEntity<ObjectResource> signupOfCollectMemberInfo(@Valid @RequestBody MemberSignupCollectMemberInfoRequestDto requestDto) {
        this.authService.collectMemberInfo(requestDto);

        SuccessCommonApiResponse response = SuccessCommonApiResponse.of("인증코드를 전송하였습니다.");
        ObjectResource resource = new ObjectResource(response);

        resource.add(linkTo(AuthController.class).slash("signup").slash("collect-member-info").withSelfRel());
        resource.add(linkTo(AuthController.class).slash("signup").slash("verify-code").withRel("verify-code"));
        resource.add(linkTo(App.class).slash("docs").slash("index.html#resources-collect-member-info").withRel("profile"));

        return ResponseEntity.ok(resource);
    }

    @PostMapping("/signup/verify-code")
    public ResponseEntity<ObjectResource> signupOfVerifyCode(@Valid @RequestBody MemberSignupVerifyCodeRequestDto requestDto, String email) {
        this.authService.verifyCode(requestDto, email);

        SuccessCommonApiResponse response = SuccessCommonApiResponse.of("인증코드가 올바르게 입력되었습니다.");
        ObjectResource resource = new ObjectResource(response);

        resource.add(linkTo(AuthController.class).slash("signup").slash("verify-code").withSelfRel());
        resource.add(linkTo(AuthController.class).slash("signup").slash("resend-code").withRel("resend-code"));
        resource.add(linkTo(AuthController.class).slash("signup").slash("setup-password").withRel("setup-password"));
        resource.add(linkTo(App.class).slash("docs").slash("index.html#resources-verify-code").withRel("profile"));

        return ResponseEntity.ok(resource);
    }
}
