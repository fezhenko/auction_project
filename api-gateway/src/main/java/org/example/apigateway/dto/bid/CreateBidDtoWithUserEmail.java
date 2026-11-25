package org.example.apigateway.dto.bid;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateBidDtoWithUserEmail {
    @NotNull
    Long auctionId;

    @NotNull
    @Email
    String email;

    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;
}
