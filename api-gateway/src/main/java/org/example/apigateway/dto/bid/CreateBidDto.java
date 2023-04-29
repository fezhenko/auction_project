package org.example.apigateway.dto.bid;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class CreateBidDto {
    @NotNull
    Long auctionId;
    @NotNull
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;
}
