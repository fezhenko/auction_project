package org.example.auction.dto.buyer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
