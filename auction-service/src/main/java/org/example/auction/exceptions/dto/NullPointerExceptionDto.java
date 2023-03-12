package org.example.auction.exceptions.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class NullPointerExceptionDto {
    String message;
}
