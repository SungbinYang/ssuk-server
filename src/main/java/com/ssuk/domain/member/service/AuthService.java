package com.ssuk.domain.member.service;

import com.ssuk.domain.member.entity.MemberBaseInfo;
import com.ssuk.domain.member.entity.MemberCertificationNumber;
import com.ssuk.domain.member.model.request.MemberSignupCollectMemberInfoRequestDto;
import com.ssuk.domain.member.model.request.MemberSignupVerifyCodeRequestDto;
import com.ssuk.domain.member.repository.MemberBaseInfoRepository;
import com.ssuk.domain.member.repository.MemberCertificationNumberRepository;
import com.ssuk.domain.member.repository.MemberRepository;
import com.ssuk.global.exception.custom.BusinessException;
import com.ssuk.global.util.mail.model.EmailMessage;
import com.ssuk.global.util.mail.service.EmailService;
import com.ssuk.global.util.random.CertificationNumberGenerator;
import com.ssuk.global.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;

    private final MemberBaseInfoRepository memberBaseInfoRepository;

    private final MemberCertificationNumberRepository memberCertificationNumberRepository;

    private final EmailService emailService;

    private final CertificationNumberGenerator certificationNumberGenerator;

    private final RedisUtil redisUtil;

    private final TemplateEngine templateEngine;

    /**
     * 회원 기본정보 유효성 검사 및 비동기 인증코드 메일 전송
     * @param requestDto
     */
    public void collectMemberInfo(MemberSignupCollectMemberInfoRequestDto requestDto) {
        validateMemberInfo(requestDto); // 유효성 검사

        CompletableFuture.runAsync(() -> sendVerificationEmailAsync(requestDto));
    }

    public void verifyCode(MemberSignupVerifyCodeRequestDto requestDto, String email) {

        MemberCertificationNumber memberCertificationNumber = this.memberCertificationNumberRepository.findById(email).orElseThrow(() -> new BusinessException("정상적이지 않은 방법으로 인증코드 페이지에 접속하였습니다."));

        if (!memberCertificationNumber.getCertificationNumber().equals(requestDto.certificationNumber())) {
            throw new BusinessException("인증코드가 올바르지 않거나 만료되었습니다.");
        }

        this.memberCertificationNumberRepository.deleteById(email);
    }

    /**
     * 인증 코드 생성 및 폼 생성 및 메일 전송 로직
     * @param requestDto
     */
    @Async(value = "mailExecutor")
    public void sendVerificationEmailAsync(MemberSignupCollectMemberInfoRequestDto requestDto) {
        String certificationNumber = generateAndSetCertificationNumber(requestDto);
        EmailMessage emailMessage = prepareEmailMessage(requestDto, certificationNumber);

        sendEmail(emailMessage);
    }

    /**
     * 이메일 전송 로직
     * @param emailMessage
     */
    private void sendEmail(EmailMessage emailMessage) {
        this.emailService.sendEmail(emailMessage);
    }

    /**
     * 인증코드 생성 및 redis 저장 로직
     * @param requestDto
     * @return
     */
    private String generateAndSetCertificationNumber(MemberSignupCollectMemberInfoRequestDto requestDto) {
        try {
            String certificationNumber = this.certificationNumberGenerator.generateCertificationNumber();

            MemberCertificationNumber memberCertificationNumber = MemberCertificationNumber.builder()
                    .id(requestDto.email())
                    .certificationNumber(certificationNumber)
                    .expiredTime(300L)
                    .build();

            MemberBaseInfo memberBaseInfo = MemberBaseInfo.builder()
                    .id(requestDto.email())
                    .memberBaseInfo(requestDto)
                    .expiredTime(600L)
                    .build();

            this.memberCertificationNumberRepository.save(memberCertificationNumber);
            this.memberBaseInfoRepository.save(memberBaseInfo);

            return certificationNumber;
        } catch (Exception e) {
            throw new BusinessException("인증코드 생성 혹은 redis 저장에 실패하였습니다.", e);
        }
    }

    /**
     * 인증코드 폼 생성 로직
     * @param requestDto
     * @param certificationNumber
     * @return
     */
    private EmailMessage prepareEmailMessage(MemberSignupCollectMemberInfoRequestDto requestDto, String certificationNumber) {
        Context context = new Context();
        context.setVariable("title", "회원가입 인증코드 이메일 발송 안내드립니다.");
        context.setVariable("certificationNumber", certificationNumber);
        context.setVariable("message", "아래 인증 코드를 복사하셔서 입력해주시길 바랍니다.");
        context.setVariable("year", LocalDate.now(ZoneId.of("Asia/Seoul")).getYear());

        String message = this.templateEngine.process("mail/signup-email", context);

        return EmailMessage.builder()
                .to(requestDto.email())
                .subject("[SSUK] 회원가입 인증코드 이메일 발송 안내")
                .message(message)
                .build();
    }

    /**
     * 회원정보 validation 체크
     * @param requestDto
     */
    private void validateMemberInfo(MemberSignupCollectMemberInfoRequestDto requestDto) {
        if (this.memberRepository.existsByEmail(requestDto.email())) {
            throw new BusinessException("이미 등록된 이메일입니다.");
        }

        requestDto.validateRegistrationNumber();
    }
}
