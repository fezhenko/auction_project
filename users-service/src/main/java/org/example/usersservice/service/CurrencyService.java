package org.example.usersservice.service;

import lombok.AllArgsConstructor;

import org.example.usersservice.client.ExchangeClient;
import org.example.usersservice.dto.exchange.ConvertedValuesDto;
import org.example.usersservice.dto.exchange.CurrenciesResultDto;
import org.example.usersservice.model.Currency;
import org.example.usersservice.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CurrencyService {
    private final ExchangeClient exchangeClient;
    private final CurrencyRepository currencyRepository;

    public ConvertedValuesDto convertCurrencies(
            String apikey,
            String convertedTo,
            String convertedFrom,
            Double amount
    ) {
        return exchangeClient.convertCurrency(apikey, convertedTo, convertedFrom, amount);
    }

    public CurrenciesResultDto getCurrencies(String apiKey) {
        return exchangeClient.getCurrencies(apiKey);
    }

    public void addCurrency(String currency) {
        currencyRepository.addCurrency(currency);
    }

    public Currency getCurrencyById(Long currencyId) {
        return currencyRepository.getCurrencyById(currencyId);
    }

    public List<Currency> findAllCurrencies() {
        return currencyRepository.getAllCurrencies();
    }

    public void updateCurrency(Long currencyId, String currencyKey) {
        String updatedJson = "{\"currency\":\"%s\"}".formatted(currencyKey);
        currencyRepository.updateCurrency(currencyId, updatedJson);
    }

    public void updateCurrency(Long currencyId, String currencyKey, String currencyValue) {
        String updatedJson = "{\"currency\":\"%s\", \"currencyDescription\": \"%s\"}"
                .formatted(currencyKey, currencyValue);
        currencyRepository.updateCurrency(currencyId, updatedJson);
    }

    public void deleteCurrency(Long currencyId) {
        currencyRepository.deleteCurrency(currencyId);
    }
}
