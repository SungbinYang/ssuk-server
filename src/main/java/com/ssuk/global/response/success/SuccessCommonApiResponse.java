package com.ssuk.global.response.success;

import lombok.Getter;

@Getter
public class SuccessCommonApiResponse {

    private final String message;

    protected SuccessCommonApiResponse(final String message) {
        this.message = message;
    }

    public static SuccessCommonApiResponse of(final String message) {
        return new SuccessCommonApiResponse(message);
    }
}
