package org.example.usersservice.client;


import org.example.usersservice.dto.exchange.ConvertedValuesDto;
import org.example.usersservice.dto.exchange.CurrenciesResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service", url = "${services.exchange-service.url}")
public interface ExchangeClient {

    @RequestMapping(method = RequestMethod.GET, path = "/convert")
    ConvertedValuesDto convertCurrency(
            @RequestHeader(name = "apikey") String apiKey,
            @RequestParam("to") String convertedTo,
            @RequestParam("from") String convertedFrom,
            @RequestParam("amount") Double amount
    );

    @RequestMapping(method = RequestMethod.GET, path = "/symbols")
    CurrenciesResultDto getCurrencies(@RequestHeader(name = "apikey") String apiKey);

}
