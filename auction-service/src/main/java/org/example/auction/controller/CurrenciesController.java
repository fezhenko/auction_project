package org.example.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currencies")
public class CurrenciesController {
    private final CurrenciesService currenciesService;
    private final ObjectMapper objectMapper;
    private final CurrencyConverter currencyConverter;
    private final String apiKey;

    @Autowired
    public CurrenciesController(CurrenciesService currenciesService,
                                ObjectMapper objectMapper,
                                CurrencyConverter currencyConverter,
                                @Value("${services.exchange-service.api-key}") String apiKey) {
        this.currenciesService = currenciesService;
        this.objectMapper = objectMapper;
        this.currencyConverter = currencyConverter;
        this.apiKey = apiKey;
    }


    @Operation(summary = "Convert a specified amount from one currency to another")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully converted"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
    @PostMapping("/convert")
    public ResponseEntity<ConvertedValuesDto> convertCurrencies(
            @Parameter(required = true) @RequestBody @Valid QueryDto queryDto) {
        ConvertedValuesDto convertResponse = currenciesService.convertCurrencies(
                apiKey, queryDto.getTo(), queryDto.getFrom(), queryDto.getAmount());
        return ResponseEntity.ok(convertResponse);
    }

    @GetMapping("/symbols")
    public ResponseEntity<CurrenciesResultDto> getCurrencies() {
        CurrenciesResultDto currenciesResultDto = currenciesService.getCurrencies(apiKey);
        return ResponseEntity.ok().body(currenciesResultDto);
    }

    @Operation(summary = "Add new currency")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Successfully added"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @SneakyThrows
    public void addCurrency(@RequestBody @Valid AddCurrencyDto addCurrencyDto) {
        currenciesService.addCurrency(objectMapper.writeValueAsString(addCurrencyDto));
    }

    @Operation(summary = "Find all existing currencies")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully find"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<List<FullCurrencyInfoDto>> findAllCurrencies() {
        List<Currency> currencies = currenciesService.findAllCurrencies();
        return ResponseEntity.ok(currencyConverter.toDto(currencies));
    }

    @Operation(summary = "Get currency information by currency id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully find"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
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

    @Operation(summary = "Update currency info in database")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{currencyId}")
    public void updateCurrency(@PathVariable("currencyId") Long currencyId,
                               @RequestBody @Valid CurrencyDto updateCurrencyDto) {
        if (updateCurrencyDto.getCurrencyValue() == null) {
            currenciesService.updateCurrency(currencyId, updateCurrencyDto.getCurrencyKey()
            );
        }
        currenciesService.updateCurrency(currencyId, updateCurrencyDto.getCurrencyKey(),
                updateCurrencyDto.getCurrencyValue()
        );
    }

    @Hidden
    @Operation(summary = "Delete a currency from database")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Currency does not exist")
            })
    @DeleteMapping("/{currencyId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCurrency(@PathVariable("currencyId") Long currencyId) {
        currenciesService.deleteCurrency(currencyId);
    }


}
