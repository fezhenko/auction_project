package org.example.apigateway.json;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.client.dto.BuyerDto;
import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.client.dto.UserVerificationDto;
import org.example.apigateway.dto.auction.AddItemToAuctionDto;
import org.example.apigateway.dto.bid.CreateBidDtoWithUserEmail;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.example.apigateway.dto.category.CategoryDto;
import org.example.apigateway.dto.currency.CurrencyDto;
import org.example.apigateway.dto.currency.FullCurrencyInfoDto;
import org.example.apigateway.dto.items.CreateItemAdjustedDto;
import org.example.apigateway.dto.items.ItemDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.example.apigateway.json.JsonRoundTrip.assertRoundTrip;

/**
 * Pins the JSON that the gateway's Feign clients send to and receive from auction-service and users-service. The
 * response bodies follow what those services return. CredentialsDto, CreateUserDto and AuctionDto travel through
 * Feign too; ControllerDtoJsonTest covers them.
 */
@JsonTest
class FeignClientDtoJsonTest {

    private static final Date CREATED_AT = Date.from(Instant.parse("2026-11-20T08:15:30Z"));
    private static final String BCRYPT_HASH = "$2a$10$aqmzCLUwQL2zP80p0RU9f.2kAFif/C6g9Yoxii2yzBUPj1SAICaIO";

    @Autowired
    private JacksonTester<CreateSellerDto> createSeller;

    @Autowired
    private JacksonTester<AddItemToAuctionDto> addItemToAuction;

    @Autowired
    private JacksonTester<CreateBidDtoWithUserEmail> createBidWithUserEmail;

    @Autowired
    private JacksonTester<CreateBuyerWithUserEmailDto> createBuyerWithUserEmail;

    @Autowired
    private JacksonTester<CreateItemAdjustedDto> createItemAdjusted;

    @Autowired
    private JacksonTester<SellerDto> seller;

    @Autowired
    private JacksonTester<BuyerDto> buyer;

    @Autowired
    private JacksonTester<ItemDto> item;

    @Autowired
    private JacksonTester<UserVerificationDto> userVerification;

    @Autowired
    private JacksonTester<AppUserDto> appUser;

    @Test
    void createSellerSentBySellerClient() throws IOException {
        assertRoundTrip(
                createSeller,
                CreateSellerDto.builder().email("user@auction.test").build(),
                """
                {"email": "user@auction.test"}
                """);
    }

    @Test
    void addItemToAuctionSentByAuctionsClient() throws IOException {
        assertRoundTrip(addItemToAuction, AddItemToAuctionDto.of("user@auction.test", 31L, 150.0), """
                {"email": "user@auction.test", "itemId": 31, "price": 150.0}
                """);
    }

    @Test
    void createBidSentByBidsClient() throws IOException {
        assertRoundTrip(
                createBidWithUserEmail,
                CreateBidDtoWithUserEmail.builder()
                        .auctionId(102L)
                        .email("user@auction.test")
                        .amount(200.5)
                        .build(),
                """
                {"auctionId": 102, "email": "user@auction.test", "amount": 200.5}
                """);
    }

    @Test
    void createBuyerSentByBuyerClient() throws IOException {
        assertRoundTrip(
                createBuyerWithUserEmail,
                CreateBuyerWithUserEmailDto.builder()
                        .username("user@auction.test")
                        .auctionId(102L)
                        .build(),
                """
                {"email": "user@auction.test", "auctionId": 102}
                """);
    }

    @Test
    void createItemSentByItemClient() throws IOException {
        assertRoundTrip(
                createItemAdjusted,
                CreateItemAdjustedDto.builder()
                        .description("Oak chair")
                        .price(150.0)
                        .ownerId(2L)
                        .build(),
                """
                {"description": "Oak chair", "price": 150.0, "ownerId": 2}
                """);
    }

    @Test
    void sellerReceivedBySellerAndAuctionsClients() throws IOException {
        assertRoundTrip(
                seller,
                SellerDto.builder()
                        .sellerId(7L)
                        .auctionId(102L)
                        .createdAt(CREATED_AT)
                        .email("user@auction.test")
                        .build(),
                """
                {
                  "sellerId": 7,
                  "auctionId": 102,
                  "created_at": "2026-11-20T08:15:30.000+00:00",
                  "email": "user@auction.test"
                }
                """);
    }

    @Test
    void buyerReceivedByAuctionsClient() throws IOException {
        assertRoundTrip(
                buyer,
                BuyerDto.builder()
                        .id(12L)
                        .bidId(null)
                        .createdAt(CREATED_AT)
                        .email("user@auction.test")
                        .build(),
                """
                {
                  "id": 12,
                  "bidId": null,
                  "createdAt": "2026-11-20T08:15:30.000+00:00",
                  "email": "user@auction.test"
                }
                """);
    }

    @Test
    void itemReceivedByItemClient() throws IOException {
        assertRoundTrip(
                item,
                ItemDto.builder()
                        .id(31L)
                        .description("Oak chair")
                        .itemState("ON_SELL")
                        .price(150.0)
                        .currencyDto(FullCurrencyInfoDto.builder()
                                .id(1L)
                                .currency(CurrencyDto.builder()
                                        .currencyKey("PLN")
                                        .currencyValue("Polish Zloty")
                                        .build())
                                .build())
                        .categoryDto(CategoryDto.builder()
                                .id(3L)
                                .categoryName("Furniture")
                                .build())
                        .createdAt(CREATED_AT)
                        .build(),
                """
                {
                  "id": 31,
                  "description": "Oak chair",
                  "itemState": "ON_SELL",
                  "price": 150.0,
                  "currencyDto": {"id": 1, "currency": {"currency": "PLN", "currencyDescription": "Polish Zloty"}},
                  "categoryDto": {"id": 3, "categoryName": "Furniture"},
                  "createdAt": "2026-11-20T08:15:30.000+00:00"
                }
                """);
    }

    @Test
    void userVerificationReceivedByUserClient() throws IOException {
        assertRoundTrip(
                userVerification,
                UserVerificationDto.builder()
                        .email("user@auction.test")
                        .password(BCRYPT_HASH)
                        .build(),
                """
                {"email": "user@auction.test", "password": "%s"}
                """.formatted(BCRYPT_HASH));
    }

    @Test
    void appUserReceivedByUserClient() throws IOException {
        assertRoundTrip(
                appUser,
                AppUserDto.builder()
                        .id(1L)
                        .firstname(null)
                        .lastname(null)
                        .password(BCRYPT_HASH)
                        .email("admin@auction.test")
                        .role("ADMIN")
                        .balance(0.0)
                        .phoneNumber(null)
                        .createdAt(CREATED_AT)
                        .build(),
                """
                {
                  "id": 1,
                  "firstname": null,
                  "lastname": null,
                  "password": "%s",
                  "email": "admin@auction.test",
                  "role": "ADMIN",
                  "balance": 0.0,
                  "phoneNumber": null,
                  "createdAt": "2026-11-20T08:15:30.000+00:00"
                }
                """.formatted(BCRYPT_HASH));
    }
}
