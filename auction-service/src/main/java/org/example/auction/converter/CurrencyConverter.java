package org.example.auction.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.auction.dto.currency.CurrencyDto;
import org.example.auction.dto.currency.FullCurrencyInfoDto;
import org.example.auction.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Component
public interface CurrencyConverter {

    @Mapping(source = "Currency", target = "FullCurrencyInfoDto", qualifiedByName = "CurrencyToDto")
    @Named("CurrencyToDto")
    default List<FullCurrencyInfoDto> toDto(List<Currency> currencies) throws RuntimeException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FullCurrencyInfoDto> currenciesList = new ArrayList<>();
        for (Currency currency : currencies) {
            FullCurrencyInfoDto fullCurrencyInfoDto = FullCurrencyInfoDto.builder()
                    .id(currency.getId())
                    .currency(objectMapper.readValue(currency.getCurrency().toString(), CurrencyDto.class))
                    .build();
            currenciesList.add(fullCurrencyInfoDto);
        }
        return currenciesList;
    }

}
