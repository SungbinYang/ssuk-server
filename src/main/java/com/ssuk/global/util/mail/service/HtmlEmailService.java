package com.ssuk.global.util.mail.service;

import com.ssuk.global.exception.custom.BusinessException;
import com.ssuk.global.util.mail.model.EmailMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true);
            mimeMessageHelper.setFrom(new InternetAddress("no-reply@ssuk.com", "쓱 운영지원사업팀"));

            this.javaMailSender.send(mimeMessage);

            log.info("sent email");
        } catch (Exception e) {
            log.error("failed to send email", e);
            throw new BusinessException("이메일 전송에 실패하였습니다.");
        }
    }
}
