package com.ssuk.global.util.mail.service;

import com.ssuk.global.util.mail.model.EmailMessage;

@FunctionalInterface
public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
