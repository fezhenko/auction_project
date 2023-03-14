package org.example.auction.dto.seller;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateSellerDto {
    @NotNull
    Long auctionId;
}
