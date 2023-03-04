package org.example.usersservice.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Value
@Jacksonized
@Builder
public class UpdateUserNonMandatoryFieldsDto {
    @Size(min = 3, max = 20)
    String firstname;
    @Size(min = 3, max = 20)
    String lastname;
    @Size(min = 6)
    String phoneNumber;
}
