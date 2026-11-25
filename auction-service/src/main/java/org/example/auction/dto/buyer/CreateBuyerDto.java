package org.example.auction.dto.buyer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CreateBuyerDto {
    @NotNull
    @Email
    String email;

    @NotNull
    Long auctionId;
}
