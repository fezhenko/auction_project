package org.example.usersservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.dto.ValidationErrorDto;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ValidationErrorDto> handleValidationException(final MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("%s".formatted(methodArgumentNotValidException.getFieldError()));
        return ResponseEntity.badRequest().body(new ValidationErrorDto("%s".formatted(methodArgumentNotValidException.getLocalizedMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorDto> handleClassCastException(final ClassCastException classCastException) {
        log.error(classCastException.getLocalizedMessage());
        return ResponseEntity.badRequest().body(new ValidationErrorDto(classCastException.getLocalizedMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorDto> handlePSQLException(final PSQLException psqlException) {
        log.error(psqlException.getLocalizedMessage());
        return ResponseEntity.badRequest().body(new ValidationErrorDto(psqlException.getLocalizedMessage()));
    }
}

