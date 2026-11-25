package org.example.usersservice.dto.exchange;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class InfoDto {
    @NotNull
    Long timestamp;

    @NotNull
    Double rate;
}
