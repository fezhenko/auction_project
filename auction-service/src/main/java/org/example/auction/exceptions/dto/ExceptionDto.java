package org.example.auction.exceptions.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;

@Value
@Jacksonized
@Builder
public class ExceptionDto {
    String message;
    HttpStatus status;
}
