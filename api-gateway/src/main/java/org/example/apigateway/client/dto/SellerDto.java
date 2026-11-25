package org.example.apigateway.client.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

@Jacksonized
@Builder
@Value
public class SellerDto {
    @NotNull
    Long sellerId;

    @Nullable
    Long auctionId;

    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "mm-dd-yyyy")
    Date createdAt;

    @Email
    String email;
}
