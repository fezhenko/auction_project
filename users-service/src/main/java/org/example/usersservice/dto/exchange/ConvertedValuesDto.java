package org.example.usersservice.dto.exchange;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

@Value
@Jacksonized
@Builder
@JsonPropertyOrder({"result", "query", "info", "date", "success"})
public class ConvertedValuesDto {
    boolean success;
    QueryDto query;
    InfoDto info;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date date;

    Double result;
}
