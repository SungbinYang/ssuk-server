package com.ssuk.global.util.random;

import com.ssuk.global.exception.custom.BusinessException;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class RandomCertificationNumberGenerator implements CertificationNumberGenerator {

    @Override
    public String generateCertificationNumber() {
        try {
            int certificationNumber = SecureRandom.getInstanceStrong().nextInt(90000) + 100000;

            return String.valueOf(certificationNumber);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("인증코드 생성에 실패하였습니다.", e);
        }
    }
}
