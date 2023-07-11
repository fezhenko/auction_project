package org.example.apigateway.exceptions;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
public class ErrorMessageDto {
    String errorMessage;
}
