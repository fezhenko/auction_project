package org.example.auction.dto.buyer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Jacksonized
@Builder
public class BuyerDto {
    @NotNull
    Long id;
    Long bidId;
    Date createdAt;
}
