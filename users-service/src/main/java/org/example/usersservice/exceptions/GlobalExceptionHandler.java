package org.example.usersservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.dto.users.ExceptionDto;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleValidationException(
            final MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        log.error("%s".formatted(methodArgumentNotValidException.getFieldError()));
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(methodArgumentNotValidException.getLocalizedMessage()))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleClassCastException(final ClassCastException classCastException) {
        log.error(classCastException.getLocalizedMessage());
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(classCastException.getLocalizedMessage()))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handlePSQLException(final PSQLException psqlException) {
        log.error(psqlException.getLocalizedMessage());
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(psqlException.getLocalizedMessage()))
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUserBalanceLessThanPriceException(final UserBalanceIsLessThanPriceException ex) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(ex.getMessage()))
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUserIdIsNullException(final UserIdIsNullException ex) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(ex.getMessage()))
                        .build());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleItemIsNullException(final ItemIsNullException ex) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .error("%s".formatted(ex.getMessage()))
                        .build());
    }
}
