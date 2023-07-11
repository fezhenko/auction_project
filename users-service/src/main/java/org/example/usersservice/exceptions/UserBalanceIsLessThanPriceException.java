package org.example.usersservice.exceptions;

public class UserBalanceIsLessThanPriceException extends RuntimeException {

    public UserBalanceIsLessThanPriceException(String message) {
        super(message);
    }
}
