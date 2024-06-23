package com.ssuk.global.exception.custom;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationException {

    private String field;

    private String value;

    private String reason;

    public static List<ValidationException> of(String field, String value, String reason) {
        final List<ValidationException> validationExceptions = new ArrayList<>();
        validationExceptions.add(new ValidationException(field, value, reason));

        return validationExceptions;
    }

    public static List<ValidationException> of(BindingResult bindingResult) {
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        return fieldErrors.stream().map(fieldError -> new ValidationException(fieldError.getField(),
                fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                fieldError.getDefaultMessage())).toList();
    }
}
