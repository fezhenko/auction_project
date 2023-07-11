package org.example.auction.exceptions;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.exceptions.auction.AuctionDateUpdatedByPastDateException;
import org.example.auction.exceptions.auction.AuctionDoesNotExistException;
import org.example.auction.exceptions.auction.AuctionStatusCannotBeUpdatedException;
import org.example.auction.exceptions.auction.CurrentPriceDoesNotMatchWithMinimalBidException;
import org.example.auction.exceptions.auction.CurrentPriceLessThanStartPriceException;
import org.example.auction.exceptions.auction.ItemAlreadyExistException;
import org.example.auction.exceptions.auction.ItemIsNotAddedToAuctionException;
import org.example.auction.exceptions.seller.SellerEmailDoesNotMatchWithOwnerEmailException;
import org.example.auction.exceptions.seller.SellerIdIsNullException;
import org.example.auction.exceptions.bid.AddBidToNotStartedAuctionException;
import org.example.auction.exceptions.bid.BidAmountIsZeroException;
import org.example.auction.exceptions.bid.BidAmountLessThanCurrentPriceException;
import org.example.auction.exceptions.bid.BidDoesNotExistException;
import org.example.auction.exceptions.buyer.BuyerDoesNotExistException;
import org.example.auction.exceptions.dto.ExceptionDto;
import org.example.auction.exceptions.seller.UserEmailIsNullException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handlePSQLException(final PSQLException psqlException) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder().message(INTERNAL_SERVER_ERROR).build()
        );
    }

    @Override
    @ResponseBody
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder().message(INTERNAL_SERVER_ERROR).status(status).build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleAuctionDoesNotExistException(AuctionDoesNotExistException exception) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleAuctionDateUpdatedByPastDateException(
            AuctionDateUpdatedByPastDateException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleItemAlreadyExistException(ItemAlreadyExistException exception) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleSellerEmailDoesNotMatchWithOwnerEmailException(
            SellerEmailDoesNotMatchWithOwnerEmailException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleSellerIdIsNullException(SellerIdIsNullException exception) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleItemIsNotAddedToAuctionException(
            ItemIsNotAddedToAuctionException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleCurrentPriceLessThanStartPriceException(
            CurrentPriceLessThanStartPriceException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleCurrentPriceDoesNotMatchWithMinimalBidException(
            CurrentPriceDoesNotMatchWithMinimalBidException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleAuctionStatusCannotBeUpdatedException(
            AuctionStatusCannotBeUpdatedException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleBidAmountIsZeroException(
            BidAmountIsZeroException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleBuyerDoesNotExistException(
            BuyerDoesNotExistException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleAddBidToNotStartedAuctionException(
            AddBidToNotStartedAuctionException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleBidAmountLessThanCurrentPrice(
            BidAmountLessThanCurrentPriceException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleBidDoesNotExistException(
            BidDoesNotExistException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleUserEmailIsNullException(
            UserEmailIsNullException exception
    ) {
        return constructBadRequestEntityWithErrorMessage(exception);
    }

    private ResponseEntity<ExceptionDto> constructBadRequestEntityWithErrorMessage(RuntimeException e) {
        return ResponseEntity.badRequest().body(
                ExceptionDto.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST).build()
        );
    }
}
