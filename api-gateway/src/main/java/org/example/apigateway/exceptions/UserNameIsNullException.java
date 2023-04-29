package org.example.apigateway.exceptions;

public class UserNameIsNullException extends RuntimeException {

    public UserNameIsNullException(String message) {
        super(message);
    }
}
