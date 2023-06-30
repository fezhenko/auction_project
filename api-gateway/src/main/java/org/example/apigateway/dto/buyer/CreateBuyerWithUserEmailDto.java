package org.example.apigateway.dto.buyer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value
@Jacksonized
@Builder
public class CreateBuyerWithUserEmailDto {
    @NotNull
    @Email
    @JsonProperty("email")
    String username;
    @NotNull
    Long auctionId;
}
