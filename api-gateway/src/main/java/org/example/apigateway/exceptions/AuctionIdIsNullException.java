package org.example.apigateway.exceptions;

public class AuctionIdIsNullException extends RuntimeException {

    public AuctionIdIsNullException(String message) {
        super(message);
    }
}
