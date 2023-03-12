package org.example.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.auction.converter.CurrencyConverter;
import org.example.auction.dto.AddCurrencyDto;
import org.example.auction.dto.CurrencyDto;
import org.example.auction.dto.FullCurrencyInfoDto;
import org.example.auction.dto.exchange.ConvertedValuesDto;
import org.example.auction.dto.exchange.CurrenciesResultDto;
import org.example.auction.dto.exchange.QueryDto;
import org.example.auction.model.Currency;
import org.example.auction.service.CurrenciesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@AllArgsConstructor
public class CurrenciesController {
    private final CurrenciesService currenciesService;
    private final ObjectMapper objectMapper;

    private final CurrencyConverter currencyConverter;

    @PostMapping("/convert")
    public ResponseEntity<ConvertedValuesDto> convertCurrencies(
            @Value("${services.exchange-service.api-key}") String apiKey,
            @RequestBody QueryDto queryDto) {
        ConvertedValuesDto convertResponse = currenciesService.convertCurrencies(
                apiKey, queryDto.getTo(), queryDto.getFrom(), queryDto.getAmount());
        return ResponseEntity.ok(convertResponse);
    }

    @GetMapping("/symbols")
    public ResponseEntity<CurrenciesResultDto> getCurrencies(
            @Value("${services.exchange-service.api-key}") String apiKey) {
        CurrenciesResultDto currenciesResultDto = currenciesService.getCurrencies(apiKey);
        return ResponseEntity.ok().body(currenciesResultDto);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public void addCurrency(@RequestBody AddCurrencyDto addCurrencyDto) {
        currenciesService.addCurrency(objectMapper.writeValueAsString(addCurrencyDto));
    }

    @GetMapping()
    @SneakyThrows
    public ResponseEntity<List<FullCurrencyInfoDto>> findAllCurrencies() {
        List<Currency> currencies = currenciesService.findAllCurrencies();
        return ResponseEntity.ok(currencyConverter.toDto(currencies));
    }

    @GetMapping("/{currencyId}")
    @SneakyThrows
    public ResponseEntity<FullCurrencyInfoDto> findCurrencyById(@PathVariable("currencyId") Long currencyId) {
        Currency currency = currenciesService.getCurrencyById(currencyId);
        CurrencyDto currencyDto = objectMapper.readValue(currency.getCurrency().toString(), CurrencyDto.class);
        return ResponseEntity.ok(
                FullCurrencyInfoDto.builder()
                        .id(currency.getId())
                        .currency(currencyDto)
                        .build());
    }

}
