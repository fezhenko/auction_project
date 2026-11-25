package org.example.auction.dto.buyer;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class BuyerDto {
    @NotNull
    Long id;

    @Email
    String email;

    Long bidId;
    Date createdAt;
}
