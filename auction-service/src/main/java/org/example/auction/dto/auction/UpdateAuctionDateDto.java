package org.example.auction.dto.auction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Value
@Builder
@Jacksonized
public class UpdateAuctionDateDto {
    @NotNull
    @DateTimeFormat(iso = DATE_TIME)
    Date auctionDate;
}
