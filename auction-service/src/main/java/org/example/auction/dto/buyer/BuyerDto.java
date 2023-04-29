package org.example.auction.dto.buyer;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
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
