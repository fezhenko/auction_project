package org.example.apigateway.json;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;

import org.example.apigateway.client.dto.AuthResultDto;
import org.example.apigateway.client.dto.CredentialsDto;
import org.example.apigateway.dto.CreateUserDto;
import org.example.apigateway.dto.UserDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.AuctionItemDto;
import org.example.apigateway.dto.auction.PriceDto;
import org.example.apigateway.dto.bid.CreateBidDto;
import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.dto.items.CreateItemDto;
import org.example.apigateway.exceptions.ErrorMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.apigateway.json.JsonRoundTrip.assertRoundTrip;

/**
 * Pins the JSON that the gateway's controllers read and write: request bodies, response bodies and the two error
 * bodies of GatewayExceptionHandler. It checks field names, nulls and the {@code java.util.Date} format of the
 * application's auto-configured Jackson mapper, so a change of Jackson version can't change the public API unnoticed.
 */
@JsonTest
class ControllerDtoJsonTest {

    private static final Date AUCTION_DATE = Date.from(Instant.parse("2026-11-24T12:00:00Z"));
    private static final Date CREATED_AT = Date.from(Instant.parse("2026-11-20T08:15:30Z"));

    @Autowired
    private JacksonTester<CredentialsDto> credentials;

    @Autowired
    private JacksonTester<CreateUserDto> createUser;

    @Autowired
    private JacksonTester<CreateBuyerDto> createBuyer;

    @Autowired
    private JacksonTester<CreateBidDto> createBid;

    @Autowired
    private JacksonTester<CreateItemDto> createItem;

    @Autowired
    private JacksonTester<AddItemDto> addItem;

    @Autowired
    private JacksonTester<AuthResultDto> authResult;

    @Autowired
    private JacksonTester<UserDto> user;

    @Autowired
    private JacksonTester<AuctionDto> auction;

    @Autowired
    private JacksonTester<ErrorMessageDto> errorMessage;

    @Autowired
    private JacksonTester<ProblemDetail> problemDetail;

    @Test
    void credentialsOfPostAuth() throws IOException {
        assertRoundTrip(
                credentials,
                CredentialsDto.builder()
                        .email("user@auction.test")
                        .password("test-password")
                        .build(),
                """
                {"email": "user@auction.test", "password": "test-password"}
                """);
    }

    @Test
    void createUserOfPostUsers() throws IOException {
        assertRoundTrip(
                createUser,
                CreateUserDto.builder()
                        .email("user@auction.test")
                        .password("test-password")
                        .role("USER")
                        .build(),
                """
                {"email": "user@auction.test", "password": "test-password", "role": "USER"}
                """);
    }

    @Test
    void createBuyerOfPostBuyers() throws IOException {
        assertRoundTrip(createBuyer, CreateBuyerDto.builder().auctionId(102L).build(), """
                {"auctionId": 102}
                """);
    }

    @Test
    void createBidOfPostBids() throws IOException {
        assertRoundTrip(
                createBid, CreateBidDto.builder().auctionId(102L).amount(200.5).build(), """
                {"auctionId": 102, "amount": 200.5}
                """);
    }

    @Test
    void createItemOfPostItems() throws IOException {
        assertRoundTrip(
                createItem,
                CreateItemDto.builder().description("Oak chair").price(150.0).build(),
                """
                {"description": "Oak chair", "price": 150.0}
                """);
    }

    @Test
    void addItemOfPostAuctionItem() throws IOException {
        assertRoundTrip(addItem, AddItemDto.builder().itemId(31L).build(), """
                {"itemId": 31}
                """);
    }

    @Test
    void authResultOfPostAuth() throws IOException {
        assertRoundTrip(
                authResult,
                AuthResultDto.builder().token("header.payload.signature").build(),
                """
                {"token": "header.payload.signature"}
                """);
    }

    @Test
    void userOfGetUsers() throws IOException {
        assertRoundTrip(
                user,
                UserDto.builder()
                        .id(2L)
                        .firstname("Test")
                        .lastname(null)
                        .email("user@auction.test")
                        .role("USER")
                        .balance(500.0)
                        .phoneNumber("+48500100200")
                        .createdAt(CREATED_AT)
                        .build(),
                """
                {
                  "id": 2,
                  "firstname": "Test",
                  "lastname": null,
                  "email": "user@auction.test",
                  "role": "USER",
                  "balance": 500.0,
                  "phoneNumber": "+48500100200",
                  "createdAt": "2026-11-20T08:15:30.000+00:00"
                }
                """);
    }

    @Test
    void auctionOfGetAndPostAuctions() throws IOException {
        assertRoundTrip(
                auction,
                AuctionDto.builder()
                        .auctionId(102L)
                        .auctionDate(AUCTION_DATE)
                        .auctionState("IN_PROGRESS")
                        .itemDto(AuctionItemDto.builder()
                                .id(31L)
                                .description(null)
                                .seller(7L)
                                .build())
                        .priceDto(PriceDto.builder()
                                .startPrice(150.0)
                                .minimalBid(10.0)
                                .currentPrice(190.0)
                                .finalPrice(null)
                                .buyer(12L)
                                .build())
                        .isPayed(false)
                        .createdAt(CREATED_AT)
                        .build(),
                """
                {
                  "id": 102,
                  "auctionDate": "2026-11-24T12:00:00.000+00:00",
                  "status": "IN_PROGRESS",
                  "item": {"id": 31, "description": null, "seller": 7},
                  "prices": {
                    "startPrice": 150.0,
                    "minimalBid": 10.0,
                    "currentPrice": 190.0,
                    "finalPrice": null,
                    "buyer": 12
                  },
                  "isPayed": false,
                  "createdAt": "2026-11-20T08:15:30.000+00:00"
                }
                """);
    }

    @Test
    void errorMessageOfGatewayExceptionHandler() throws IOException {
        assertRoundTrip(
                errorMessage,
                ErrorMessageDto.builder()
                        .errorMessage("Auction creation fails, please try again later")
                        .build(),
                """
                {"errorMessage": "Auction creation fails, please try again later"}
                """);
    }

    /**
     * The body Spring MVC's exception handling writes for 400, 404, 405 and 415. ProblemDetail gets its JSON shape from
     * a Jackson mixin that the auto-configured mapper registers. The gateway only writes this body, so only that
     * direction is checked.
     */
    @Test
    void problemDetailOfGatewayExceptionHandler() throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Failed to read request");
        problem.setInstance(URI.create("/api/v1/auth"));

        assertThat(problemDetail.write(problem)).isStrictlyEqualToJson("""
                {
                  "type": "about:blank",
                  "title": "Bad Request",
                  "status": 400,
                  "detail": "Failed to read request",
                  "instance": "/api/v1/auth"
                }
                """);
    }
}
