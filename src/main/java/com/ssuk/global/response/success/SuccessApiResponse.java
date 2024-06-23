package com.ssuk.global.response.success;

import lombok.Getter;

@Getter
public class SuccessApiResponse<T> extends SuccessCommonApiResponse {

    private final T data;

    private SuccessApiResponse(final String message, final T data) {
        super(message);
        this.data = data;
    }

    public static <T> SuccessApiResponse<T> success(final String message, final T data) {
        return new SuccessApiResponse<>(message, data);
    }
}
