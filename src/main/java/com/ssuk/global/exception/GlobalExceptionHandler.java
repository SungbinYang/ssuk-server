package com.ssuk.global.exception;

import com.ssuk.global.exception.custom.BusinessException;
import com.ssuk.global.response.error.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.ssuk.global.exception.GlobalExceptionCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Java Bean Validation 예외 핸들링
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handle MethodArgumentNotValidException", e);
        return new ResponseEntity<>(ExceptionResponse.of(INVALID_REQUEST_PARAMETER, e.getBindingResult()),
                INVALID_REQUEST_PARAMETER.getHttpStatus());
    }

    /**
     * 유효하지 않은 클라이언트의 요청 값 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handle IllegalArgumentException", e);
        return new ResponseEntity<>(
                ExceptionResponse.of(INVALID_REQUEST_PARAMETER, e.getMessage()),
                INVALID_REQUEST_PARAMETER.getHttpStatus()
        );
    }

    /**
     * Business Exception 예외 핸들링
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
        log.error("handle BusinessException : {}", e.getMessage());
        return new ResponseEntity<>(
                ExceptionResponse.of(INVALID_REQUEST_PARAMETER, e.getMessage()),
                INVALID_REQUEST_PARAMETER.getHttpStatus()
        );
    }

    /**
     * 잘못된 HTTP Method 요청 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handle HttpRequestMethodNotSupportedException", e);
        return new ResponseEntity<>(
                ExceptionResponse.of(INVALID_REQUEST_METHOD),
                INVALID_REQUEST_METHOD.getHttpStatus()
        );
    }

    /**
     * 유효하지 않은 handler 요청 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("handle NoHandlerFoundException", e);
        return new ResponseEntity<>(
                ExceptionResponse.of(NOT_FOUND_RESOURCE),
                NOT_FOUND_RESOURCE.getHttpStatus()
        );
    }

    /**
     * 잘못된 타입 변환 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ExceptionResponse> handleBindException(BindException e) {
        log.error("handle BindException", e);
        return new ResponseEntity<>(
                ExceptionResponse.of(INVALID_REQUEST_PARAMETER, e.getBindingResult()),
                INVALID_REQUEST_PARAMETER.getHttpStatus()
        );
    }

    /**
     * 모든 예외를 처리
     * 웬만해서 여기까지 오면 안됨
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("handle Exception", e);
        return new ResponseEntity<>(
                ExceptionResponse.of(SERVER_ERROR),
                SERVER_ERROR.getHttpStatus()
        );
    }
}
