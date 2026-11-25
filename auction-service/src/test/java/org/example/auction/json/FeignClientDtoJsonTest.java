package org.example.auction.json;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.example.auction.scheduler.client.dto.UpdateItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the JSON that auction-service's Feign clients exchange with users-service: field names, nulls and the
 * {@code java.util.Date} format of the application's auto-configured Jackson mapper. A change of Jackson version must
 * not change it.
 */
@JsonTest
class FeignClientDtoJsonTest {

    @Autowired
    private JacksonTester<AppUserDto> appUser;

    @Autowired
    private JacksonTester<FinalPriceDto> finalPrice;

    @Autowired
    private JacksonTester<UpdateItemDto> updateItem;

    @Test
    void appUserReceivedByUserClient() throws IOException {
        assertRoundTrip(
                appUser,
                AppUserDto.builder()
                        .id(2L)
                        .firstname("Test")
                        .lastname("User")
                        .password(null)
                        .email("user@auction.test")
                        .role("USER")
                        .balance(500.0)
                        .phoneNumber(null)
                        .createdAt(Date.from(Instant.parse("2026-11-21T09:30:00Z")))
                        .build(),
                """
                {
                  "id": 2,
                  "firstname": "Test",
                  "lastname": "User",
                  "password": null,
                  "email": "user@auction.test",
                  "role": "USER",
                  "balance": 500.0,
                  "phoneNumber": null,
                  "createdAt": "2026-11-21T09:30:00.000+00:00"
                }
                """);
    }

    @Test
    void finalPriceSentByUserClient() throws IOException {
        assertRoundTrip(finalPrice, FinalPriceDto.builder().finalPrice(625.0).build(), """
                {"finalPrice": 625.0}
                """);
    }

    @Test
    void updateItemSentByItemClient() throws IOException {
        assertRoundTrip(
                updateItem,
                UpdateItemDto.builder()
                        .description(null)
                        .itemStatus("SOLD")
                        .price(625.0)
                        .itemCategory(null)
                        .build(),
                """
                {"description": null, "itemStatus": "SOLD", "price": 625.0, "itemCategory": null}
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
