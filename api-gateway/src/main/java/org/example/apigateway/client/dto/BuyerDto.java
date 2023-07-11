package org.example.apigateway.client.dto;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Value
public class BuyerDto {
    @NotNull
    Long id;
    Long bidId;
    Date createdAt;
    @Email
    String email;
}
