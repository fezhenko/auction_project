package org.example.auction.dto.auction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class AddItemToAuctionDto {
    @NotNull
    String email;

    @NotNull
    Long itemId;

    @NotNull
    @Min(0)
    Double price;
}
