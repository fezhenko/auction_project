package org.example.apigateway.dto.auction;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public static AddItemToAuctionDto of(String email, long itemId, double price) {
        return AddItemToAuctionDto.builder().email(email).itemId(itemId).price(price).build();
    }
}
