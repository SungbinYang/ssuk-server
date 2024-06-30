package com.ssuk.domain.member.controller;

import com.ssuk.domain.member.entity.MemberBaseInfo;
import com.ssuk.domain.member.entity.MemberCertificationNumber;
import com.ssuk.domain.member.model.request.MemberSignupCollectMemberInfoRequestDto;
import com.ssuk.domain.member.model.request.MemberSignupSetupPasswordRequestDto;
import com.ssuk.domain.member.model.request.MemberSignupVerifyCodeRequestDto;
import com.ssuk.domain.member.repository.MemberBaseInfoRepository;
import com.ssuk.domain.member.repository.MemberCertificationNumberRepository;
import com.ssuk.global.controller.BaseControllerTest;
import com.ssuk.global.exception.GlobalExceptionCode;
import com.ssuk.global.util.redis.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberBaseInfoRepository memberBaseInfoRepository;

    @Autowired
    private MemberCertificationNumberRepository memberCertificationNumberRepository;

    @BeforeEach
    void setup() {
        MemberSignupCollectMemberInfoRequestDto memberInfoRequestDto = new MemberSignupCollectMemberInfoRequestDto("김철수", "KIM CHULSU", "0502283", "email@email.com");

        MemberBaseInfo memberBaseInfo = MemberBaseInfo.builder()
                .id("test@email.com")
                .memberBaseInfo(memberInfoRequestDto)
                .expiredTime(600L)
                .build();

        MemberCertificationNumber memberCertificationNumber = MemberCertificationNumber.builder()
                .id("test@email.com")
                .certificationNumber("123456")
                .expiredTime(300L)
                .build();

        this.memberBaseInfoRepository.save(memberBaseInfo);
        this.memberCertificationNumberRepository.save(memberCertificationNumber);
    }

    @AfterEach
    void afterEach() {
        this.redisUtil.deleteAllData();
    }

    @Test
    @DisplayName("회원 회원가입(기본정보 수집) 통합 테스트 - 실패(잘못된 입력 값)")
    void member_signup_of_member_info_collection_integration_test_fail_caused_by_wrong_input() throws Exception {
        MemberSignupCollectMemberInfoRequestDto requestDto = new MemberSignupCollectMemberInfoRequestDto("양", "Y", "0", "email...");

        this.mockMvc.perform(post("/api/auth/signup/collect-member-info")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("errors").exists())
                .andExpect(jsonPath("errors").isNotEmpty())
                .andExpect(jsonPath("timestamp").exists());
    }

    @ParameterizedTest
    @MethodSource("providedTestDataForSignupOfMemberInfoCollection")
    @DisplayName("회원 회원가입(기본정보 수집) 통합 테스트 - 실패(유효하지 않은 입력 값)")
    void member_signup_of_member_info_collection_integration_test_fail_caused_by_invalid_input_data(String koreanName, String englishName, String registrationNumber, String email) throws Exception {
        MemberSignupCollectMemberInfoRequestDto requestDto = new MemberSignupCollectMemberInfoRequestDto(koreanName, englishName, registrationNumber, email);

        this.mockMvc.perform(post("/api/auth/signup/collect-member-info")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(기본정보 수집) 통합 테스트 - 성공")
    void member_signup_of_member_info_collection_integration_test_success() throws Exception {
        MemberSignupCollectMemberInfoRequestDto requestDto = new MemberSignupCollectMemberInfoRequestDto("김철수", "KIM CHULSU", "0502283", "email@email.com");

        this.mockMvc.perform(post("/api/auth/signup/collect-member-info")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.verify-code.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("collect-member-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        links(
                                linkWithRel("self").description("자기 자신에 대한 링크"),
                                linkWithRel("verify-code").description("인증코드 확인 링크"),
                                linkWithRel("profile").description("REST API 문서에 대한 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description("accept header : application/hal+json;charset=UTF-8"),
                                headerWithName(CONTENT_TYPE).description("content type header : application/json;charset=UTF-8"),
                                headerWithName(CONTENT_LENGTH).description("content length header")
                        ),
                        requestFields(
                                fieldWithPath("koreanName").type(STRING).description("회원의 한국이름"),
                                fieldWithPath("englishName").type(STRING).description("회원의 영국이름"),
                                fieldWithPath("registrationNumber").type(STRING).description("회원의 주민등록번호"),
                                fieldWithPath("email").type(STRING).description("회원의 이메일")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("Content type : application/hal+json;charset=UTF-8")
                        ),
                        responseFields(
                                fieldWithPath("message").description("rest api 응답 메세제"),
                                fieldWithPath("_links.self.href").description("자기 자신에 대한 링크"),
                                fieldWithPath("_links.verify-code.href").description("인증코드 확인 링크"),
                                fieldWithPath("_links.profile.href").description("REST API 문서에 대한 링크")
                        )
                ));
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 확인) 통합 테스트 - 실패(유효하지 않은 이메일)")
    void member_signup_of_verify_code_integration_test_fail_caused_by_invalid_email() throws Exception {
        MemberSignupVerifyCodeRequestDto requestDto = new MemberSignupVerifyCodeRequestDto("123456");

        this.mockMvc.perform(post("/api/auth/signup/verify-code")
                        .queryParam("email", "email@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 확인) 통합 테스트 - 실패(잘못된 입력값)")
    void member_signup_of_verify_code_integration_test_fail_caused_by_wrong_input() throws Exception {
        MemberSignupVerifyCodeRequestDto requestDto = new MemberSignupVerifyCodeRequestDto("11");

        this.mockMvc.perform(post("/api/auth/signup/verify-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("errors").exists())
                .andExpect(jsonPath("errors").isNotEmpty())
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 확인) 통합 테스트 - 실패(유효하지 않거나 만료된 인증코드)")
    void member_signup_of_verify_code_integration_test_fail_caused_by_invalid_or_expired_certification_number() throws Exception {
        MemberSignupVerifyCodeRequestDto requestDto = new MemberSignupVerifyCodeRequestDto("111111");

        this.mockMvc.perform(post("/api/auth/signup/verify-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 확인) 통합 테스트 - 성공")
    void member_signup_of_verify_code_integration_test_success() throws Exception {
        MemberSignupVerifyCodeRequestDto requestDto = new MemberSignupVerifyCodeRequestDto("123456");

        this.mockMvc.perform(post("/api/auth/signup/verify-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.resend-code.href").exists())
                .andExpect(jsonPath("_links.setup-password.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("verify-code",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                List.of(parameterWithName("email").description("기본정보 입력에 입력한 회원 이메일"))
                        ),
                        links(
                                linkWithRel("self").description("자기 자신에 대한 링크"),
                                linkWithRel("resend-code").description("인증코드 재전송 링크"),
                                linkWithRel("setup-password").description("비밀번호 설정 링크"),
                                linkWithRel("profile").description("REST API 문서에 대한 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description("accept header : application/hal+json;charset=UTF-8"),
                                headerWithName(CONTENT_TYPE).description("content type header : application/json;charset=UTF-8"),
                                headerWithName(CONTENT_LENGTH).description("content length header")
                        ),
                        requestFields(
                                fieldWithPath("certificationNumber").type(STRING).description("이메일로 전달 받은 인증코드 6자리")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("Content type : application/hal+json;charset=UTF-8")
                        ),
                        responseFields(
                                fieldWithPath("message").description("rest api 응답 메세제"),
                                fieldWithPath("_links.self.href").description("자기 자신에 대한 링크"),
                                fieldWithPath("_links.resend-code.href").description("인증코드 재전송 링크"),
                                fieldWithPath("_links.setup-password.href").description("비밀번호 설정 링크"),
                                fieldWithPath("_links.profile.href").description("REST API 문서에 대한 링크")
                        )
                ));
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 재전송) 통합 테스트 - 실패(유효하지 않은 이메일)")
    void member_resend_code_integration_test_fail_caused_by_resend_within_30_min() throws Exception {
        this.mockMvc.perform(post("/api/auth/signup/resend-code")
                        .queryParam("email", "email@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 재전송) 통합 테스트 - 실패(재전송 한번 발송 후 30분내로 재발송 시 예외)")
    void member_resend_code_integration_test_fail_caused_by_invalid_email() throws Exception {

        this.mockMvc.perform(post("/api/auth/signup/resend-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/api/auth/signup/resend-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(이메일 인증코드 재전송) 통합 테스트 - 성공")
    void member_resend_code_integration_test_success() throws Exception {
        this.mockMvc.perform(post("/api/auth/signup/resend-code")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.setup-password.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("resend-code",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                List.of(parameterWithName("email").description("기본정보 입력에 입력한 회원 이메일"))
                        ),
                        links(
                                linkWithRel("self").description("자기 자신에 대한 링크"),
                                linkWithRel("setup-password").description("비밀번호 설정 링크"),
                                linkWithRel("profile").description("REST API 문서에 대한 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description("accept header : application/hal+json;charset=UTF-8"),
                                headerWithName(CONTENT_TYPE).description("content type header : application/json;charset=UTF-8")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("Content type : application/hal+json;charset=UTF-8")
                        ),
                        responseFields(
                                fieldWithPath("message").description("rest api 응답 메세제"),
                                fieldWithPath("_links.self.href").description("자기 자신에 대한 링크"),
                                fieldWithPath("_links.setup-password.href").description("비밀번호 설정 링크"),
                                fieldWithPath("_links.profile.href").description("REST API 문서에 대한 링크")
                        )
                ));
    }

    @Test
    @DisplayName("회원 회원가입(비밀번호 설정 확인) 통합 테스트 - 실패(잘못된 입력값)")
    void member_setup_password_integration_test_fail_caused_by_wrong_input() throws Exception {
        MemberSignupSetupPasswordRequestDto requestDto = new MemberSignupSetupPasswordRequestDto("1", "1");

        this.mockMvc.perform(post("/api/auth/signup/setup-password")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("errors").exists())
                .andExpect(jsonPath("errors").isNotEmpty())
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(비밀번호 설정 확인) 통합 테스트 - 실패(유효하지 않은 이메일)")
    void member_setup_password_integration_test_fail_caused_by_invalid_email() throws Exception {
        MemberSignupSetupPasswordRequestDto requestDto = new MemberSignupSetupPasswordRequestDto("1234A", "1234A");

        this.mockMvc.perform(post("/api/auth/signup/setup-password")
                        .queryParam("email", "email@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(비밀번호 설정 확인) 통합 테스트 - 실패(비밀번호 불일치)")
    void member_setup_password_integration_test_fail_caused_by_not_equals_password_and_confirm_password() throws Exception {
        MemberSignupSetupPasswordRequestDto requestDto = new MemberSignupSetupPasswordRequestDto("1234A", "1234B");

        this.mockMvc.perform(post("/api/auth/signup/setup-password")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("status").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getHttpStatus().name()))
                .andExpect(jsonPath("code").value(GlobalExceptionCode.INVALID_REQUEST_PARAMETER.getCode()))
                .andExpect(jsonPath("timestamp").exists());
    }

    @Test
    @DisplayName("회원 회원가입(비밀번호 설정 확인) 통합 테스트 - 성공")
    void member_setup_password_integration_test_success() throws Exception {
        MemberSignupSetupPasswordRequestDto requestDto = new MemberSignupSetupPasswordRequestDto("1234A", "1234A");

        this.mockMvc.perform(post("/api/auth/signup/setup-password")
                        .queryParam("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                        .accept(MediaTypes.HAL_JSON + ";charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.login.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("setup-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                List.of(parameterWithName("email").description("기본정보 입력에 입력한 회원 이메일"))
                        ),
                        links(
                                linkWithRel("self").description("자기 자신에 대한 링크"),
                                linkWithRel("login").description("로그인 처리 링크"),
                                linkWithRel("profile").description("REST API 문서에 대한 링크")
                        ),
                        requestHeaders(
                                headerWithName(ACCEPT).description("accept header : application/hal+json;charset=UTF-8"),
                                headerWithName(CONTENT_TYPE).description("content type header : application/json;charset=UTF-8")
                        ),
                        requestFields(
                                fieldWithPath("password").type(STRING).description("회원이 입력한 비밀번호"),
                                fieldWithPath("confirmPassword").type(STRING).description("회원이 입력한 비밀번호(확인)")
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("Location header : URL where you can view the newly created member"),
                                headerWithName(CONTENT_TYPE).description("Content type : application/hal+json;charset=UTF-8")
                        ),
                        responseFields(
                                fieldWithPath("message").description("rest api 응답 메세제"),
                                fieldWithPath("_links.self.href").description("자기 자신에 대한 링크"),
                                fieldWithPath("_links.login.href").description("로그인 처리 링크"),
                                fieldWithPath("_links.profile.href").description("REST API 문서에 대한 링크")
                        )
                ));
    }

    private static Stream<Arguments> providedTestDataForSignupOfMemberInfoCollection() {
        return Stream.of(
                Arguments.of("김철수", "KIM CHULSU", "0502293", "email@email.com"),
                Arguments.of("김철수", "KIM CHULSU", "0502283", "test@email.com")
        );
    }
}