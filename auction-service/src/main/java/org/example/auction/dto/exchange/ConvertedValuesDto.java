package org.example.auction.dto.exchange;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Value
@Jacksonized
@Builder
public class ConvertedValuesDto {
    boolean success;
    QueryDto query;
    InfoDto info;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date date;
    Double result;
}

