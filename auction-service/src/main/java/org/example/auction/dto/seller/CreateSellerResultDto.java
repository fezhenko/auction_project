package org.example.auction.dto.seller;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CreateSellerResultDto {
    String message;
}
