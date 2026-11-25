package org.example.usersservice.unit.converter;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.example.usersservice.converter.CurrencyConverter;
import org.example.usersservice.converter.ItemConverter;
import org.example.usersservice.dto.currency.CurrencyDto;
import org.example.usersservice.dto.currency.FullCurrencyInfoDto;
import org.example.usersservice.dto.item.ItemDto;
import org.example.usersservice.model.Currency;
import org.example.usersservice.model.Item;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The {@code currencies.currency} column holds JSON that was written before the move to Jackson 3: rows seeded by
 * migration V3, and rows that CurrencyController stored with Jackson 2. The converters must still read both.
 */
class StoredCurrencyJsonTest {

    /** As migration V3 seeds it: multi-line, two-space indent. */
    private static final String SEEDED = """
            {
              "currency": "EUR",
              "currencyDescription": "Euro"
            }""";

    /** As Jackson 2 wrote an AddCurrencyDto: compact, in declaration order. */
    private static final String WRITTEN_BY_JACKSON_2 =
            "{\"currency\":\"PLN\",\"currencyDescription\":\"Polish Zloty\"}";

    private final CurrencyConverter currencyConverter = new CurrencyConverter() {};
    private final ItemConverter itemConverter = new ItemConverter() {};

    @Test
    void currencyConverterReadsStoredJson() throws SQLException {
        List<FullCurrencyInfoDto> currencies = currencyConverter.toDto(
                List.of(new Currency(1L, json(SEEDED)), new Currency(3L, json(WRITTEN_BY_JACKSON_2))));

        assertThat(currencies)
                .containsExactly(
                        FullCurrencyInfoDto.builder()
                                .id(1L)
                                .currency(currency("EUR", "Euro"))
                                .build(),
                        FullCurrencyInfoDto.builder()
                                .id(3L)
                                .currency(currency("PLN", "Polish Zloty"))
                                .build());
    }

    @Test
    void itemConverterReadsStoredJson() throws SQLException {
        ItemDto item = itemConverter.toDto(Item.builder()
                .id(31L)
                .description("Oak chair")
                .price(150.0)
                .currency(3L)
                .currencyDesc(json(WRITTEN_BY_JACKSON_2))
                .itemCategory(1L)
                .categoryName("Furniture")
                .itemState("ON_SELL")
                .createdAt(new Date(0L))
                .ownerId(2L)
                .build());

        assertThat(item.getCurrencyDto())
                .isEqualTo(FullCurrencyInfoDto.builder()
                        .id(3L)
                        .currency(currency("PLN", "Polish Zloty"))
                        .build());
    }

    /** What the PostgreSQL driver returns for a {@code json} column. */
    private static PGobject json(String value) throws SQLException {
        PGobject json = new PGobject();
        json.setType("json");
        json.setValue(value);
        return json;
    }

    private static CurrencyDto currency(String key, String description) {
        return CurrencyDto.builder().currencyKey(key).currencyValue(description).build();
    }
}
