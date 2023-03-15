package org.example.auction.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.auction.exceptions.dto.ExceptionDto;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleValidationException(
            final NullPointerException nullPointerException
    ) {
        log.error(nullPointerException.getMessage());
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .message(nullPointerException.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handlePSQLException(
            final PSQLException psqlException
    ) {
        log.error(psqlException.getMessage());
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder()
                        .message(psqlException.getMessage())
                        .build());
    }

}
