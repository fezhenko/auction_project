package org.example.auction.dto.seller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Jacksonized
@Builder
public class SellerDto {

    @NotNull
    Long sellerId;
    @Embedded.Nullable
    Long auctionId;
    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "mm-dd-yyyy")
    Date createdAt;
}
