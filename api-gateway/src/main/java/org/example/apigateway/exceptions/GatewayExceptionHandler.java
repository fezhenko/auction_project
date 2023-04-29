package org.example.apigateway.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleEmptyAuctions(CreateAuctionException exception) {
        return ResponseEntity.badRequest().body(
                ErrorMessageDto.builder().errorMessage(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUserEmailIsNullException(UserEmailIsNullException exception) {
        return ResponseEntity.badRequest().body(
                ErrorMessageDto.builder().errorMessage(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUserNameIsNullException(UserNameIsNullException exception) {
        return ResponseEntity.badRequest().body(
                ErrorMessageDto.builder().errorMessage(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAuctionIdIsNullException(AuctionIdIsNullException exception) {
        return ResponseEntity.badRequest().body(
                ErrorMessageDto.builder().errorMessage(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleUserBalanceNotEnoughException(UserBalanceIsNotEnoughException exception) {
        return ResponseEntity.badRequest().body(
                ErrorMessageDto.builder().errorMessage(exception.getMessage())
                        .build()
        );
    }
}
