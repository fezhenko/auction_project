package org.example.auction.dto.seller;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.format.annotation.DateTimeFormat;

@Value
@Jacksonized
@Builder
public class SellerDto {

    @NotNull
    Long sellerId;
    @Email
    String email;
    @Embedded.Nullable
    Long auctionId;
    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "mm-dd-yyyy")
    Date createdAt;
}
