package org.example.apigateway.exceptions;

public class CreateAuctionException extends RuntimeException {

    public CreateAuctionException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
