package org.example.usersservice.dto.users;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserBalanceDto {
    Double balance;
}
