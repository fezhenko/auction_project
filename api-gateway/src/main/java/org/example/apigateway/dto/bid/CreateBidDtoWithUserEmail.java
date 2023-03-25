package org.example.apigateway.dto.bid;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Value
@Builder
@Jacksonized
public class CreateBidDtoWithUserEmail {
    @NotNull
    @Email
    String email;
    @DecimalMin(value = "0.001", message = "Please enter a valid amount more than 0")
    Double amount;

}
