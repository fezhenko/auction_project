package org.example.apigateway.exceptions;

public class UserEmailIsNullException extends RuntimeException {

    public UserEmailIsNullException(String message) {
        super(message);
    }
}
