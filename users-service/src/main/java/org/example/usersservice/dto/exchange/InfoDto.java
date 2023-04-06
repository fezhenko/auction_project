package org.example.usersservice.dto.exchange;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Jacksonized
@Builder
public class InfoDto {
    @NotNull
    Long timestamp;
    @NotNull
    Double rate;
}
