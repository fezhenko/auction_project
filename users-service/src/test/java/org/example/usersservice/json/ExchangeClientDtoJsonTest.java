package org.example.usersservice.json;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.example.usersservice.dto.exchange.ConvertedValuesDto;
import org.example.usersservice.dto.exchange.CurrenciesResultDto;
import org.example.usersservice.dto.exchange.InfoDto;
import org.example.usersservice.dto.exchange.QueryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins how the application's auto-configured Jackson mapper reads the exchange-rate API's responses, which
 * ExchangeClient receives, and writes them back out, which CurrencyController returns. The API bodies follow the
 * apilayer exchangerates_data documentation. A change of Jackson version must not change either side.
 */
@JsonTest
class ExchangeClientDtoJsonTest {

    private static final ConvertedValuesDto CONVERTED = ConvertedValuesDto.builder()
            .success(true)
            .query(QueryDto.builder().from("GBP").to("JPY").amount(25.0).build())
            .info(InfoDto.builder().timestamp(1_519_328_414L).rate(148.972_231).build())
            .date(Date.from(Instant.parse("2018-02-22T00:00:00Z")))
            .result(3724.305_775)
            .build();

    @Autowired
    private JacksonTester<ConvertedValuesDto> convertedValues;

    @Autowired
    private JacksonTester<CurrenciesResultDto> currenciesResult;

    @Test
    void convertResponseOfExchangeApiIsRead() throws IOException {
        // The API sends a date without time and an extra "historical" field that the DTO doesn't map.
        assertThat(convertedValues.parseObject("""
                        {
                          "success": true,
                          "query": {"from": "GBP", "to": "JPY", "amount": 25},
                          "info": {"timestamp": 1519328414, "rate": 148.972231},
                          "historical": "",
                          "date": "2018-02-22",
                          "result": 3724.305775
                        }
                        """)).isEqualTo(CONVERTED);
    }

    @Test
    void convertedValuesOfCurrencyController() throws IOException {
        assertRoundTrip(convertedValues, CONVERTED, """
                {
                  "result": 3724.305775,
                  "query": {"from": "GBP", "to": "JPY", "amount": 25.0},
                  "info": {"timestamp": 1519328414, "rate": 148.972231},
                  "date": "2018-02-22T00:00:00.000+00:00",
                  "success": true
                }
                """);
    }

    @Test
    void symbolsResponseOfExchangeApi() throws IOException {
        assertRoundTrip(
                currenciesResult,
                CurrenciesResultDto.builder()
                        .success(true)
                        .symbols(Map.of("PLN", "Polish Zloty", "USD", "United States Dollar"))
                        .build(),
                """
                {"success": true, "symbols": {"PLN": "Polish Zloty", "USD": "United States Dollar"}}
                """);
    }

    /**
     * Checks both directions. The value is written as exactly {@code json}: the same field names, values and nulls,
     * with no extra fields, in any order. {@code json} is read back into an equal value.
     */
    private static <T> void assertRoundTrip(JacksonTester<T> tester, T value, String json) throws IOException {
        assertThat(tester.write(value)).isStrictlyEqualToJson(json);
        assertThat(tester.parseObject(json)).isEqualTo(value);
    }
}
