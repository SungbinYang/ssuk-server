package com.ssuk.global.util.mail.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailMessage {

    private String to;

    private String subject;

    private String message;
}
