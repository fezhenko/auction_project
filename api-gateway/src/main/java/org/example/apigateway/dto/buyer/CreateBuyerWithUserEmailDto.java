package org.example.apigateway.dto.buyer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
