package org.example.apigateway.exceptions;

public class UserBalanceIsNotEnoughException extends RuntimeException {

    public UserBalanceIsNotEnoughException(String message) {
        super(message);
    }
}
