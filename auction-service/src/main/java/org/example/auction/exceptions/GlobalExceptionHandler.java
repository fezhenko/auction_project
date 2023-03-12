package org.example.auction.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.auction.exceptions.dto.NullPointerExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<NullPointerExceptionDto> handleValidationException(
            final NullPointerException nullPointerException
    ) {
        log.error(nullPointerException.getMessage());
        return ResponseEntity.badRequest().body(
                NullPointerExceptionDto.builder()
                        .message(nullPointerException.getMessage())
                        .build());
    }

}
