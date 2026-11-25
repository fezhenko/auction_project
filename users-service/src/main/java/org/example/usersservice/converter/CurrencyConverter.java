package org.example.usersservice.converter;

import java.util.ArrayList;
import java.util.List;

import org.example.usersservice.dto.currency.CurrencyDto;
import org.example.usersservice.dto.currency.FullCurrencyInfoDto;
import org.example.usersservice.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Mapper
@Component
public interface CurrencyConverter {

    @Mapping(source = "Currency", target = "FullCurrencyInfoDto", qualifiedByName = "CurrencyToDto")
    @Named("CurrencyToDto")
    default List<FullCurrencyInfoDto> toDto(List<Currency> currencies) throws RuntimeException {
        ObjectMapper objectMapper = new JsonMapper();
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
