package org.example.auction.exceptions.seller;

public class UserEmailIsNullException extends RuntimeException {

    public UserEmailIsNullException(String message) {
        super(message);
    }
}
