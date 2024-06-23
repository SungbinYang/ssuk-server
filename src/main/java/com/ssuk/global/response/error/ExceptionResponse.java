package com.ssuk.global.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssuk.global.exception.ExceptionCode;
import com.ssuk.global.exception.custom.ValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {

    private String message;

    private HttpStatus status;

    private String code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ValidationException> errors;

    private LocalDateTime timestamp;

    private ExceptionResponse(final ExceptionCode exceptionCode) {
        this.message = exceptionCode.getMessage();
        this.status = exceptionCode.getHttpStatus();
        this.code = exceptionCode.getCode();
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    private ExceptionResponse(final ExceptionCode exceptionCode, final String message) {
        this.message = message;
        this.status = exceptionCode.getHttpStatus();
        this.code = exceptionCode.getCode();
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    private ExceptionResponse(final ExceptionCode errorCode, final List<ValidationException> errors) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.errors = errors;
    }

    public static ExceptionResponse of(final ExceptionCode errorCode) {
        return new ExceptionResponse(errorCode);
    }

    public static ExceptionResponse of(final ExceptionCode errorCode, final String message) {
        return new ExceptionResponse(errorCode, message);
    }

    public static ExceptionResponse of(final ExceptionCode code, final BindingResult bindingResult) {
        return new ExceptionResponse(code, ValidationException.of(bindingResult));
    }

    public static ExceptionResponse of(final ExceptionCode errorCode, final List<ValidationException> errors) {
        return new ExceptionResponse(errorCode, errors);
    }
}
