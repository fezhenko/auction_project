package org.example.usersservice.exceptions;

public class UserIdIsNullException extends RuntimeException {

    public UserIdIsNullException(String message) {
        super(message);
    }
}
