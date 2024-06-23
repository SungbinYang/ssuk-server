package com.ssuk.global.exception;

import com.ssuk.global.common.type.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode implements EnumType, ExceptionCode {

    INVALID_REQUEST_PARAMETER(BAD_REQUEST, "SA-C-001", "유효하지 않은 요청 파라미터입니다.", "400 에러 코드"),
    UNAUTHORIZED_RESOURCE_OWNER(UNAUTHORIZED, "SA-C-002", "해당 리소스를 처리할 인증이 없습니다.", "401 에러 코드"),
    INVALID_RESOURCE_OWNER(FORBIDDEN, "SA-C-003", "해당 리소스를 처리할 권한이 없습니다.", "403 에러 코드"),
    NOT_FOUND_RESOURCE(NOT_FOUND, "SA-C-004", "해당 리소스를 찾을 수 없습니다.", "404 에러 코드"),
    INVALID_REQUEST_METHOD(METHOD_NOT_ALLOWED, "SA-C-005", "유효하지 않은 http 요청 메소드입니다.", "405 에러 코드"),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "SA-S-001", "Internal Server Error", "500 에러 코드");

    private final HttpStatus httpStatus;

    private final String code;

    private final String message;

    private final String description;

}
