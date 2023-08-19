package org.example.auction.integration.service;

import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.CreateAuctionDto;
import org.example.auction.exceptions.seller.SellerIdIsNullException;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.scheduler.client.ItemClient;
import org.example.auction.scheduler.client.UserClient;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuctionItemsTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionConverter auctionConverter;

    @Mock
    private UserClient userClient;

    @Mock
    private ItemClient itemClient;

    @Container
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        auctionService = new AuctionService(
                auctionRepository,
                auctionConverter,
                null,
                null,
                itemClient,
                userClient);
    }

    @Test
    @Disabled
    void testCreateAuction() {
        CreateAuctionDto createAuctionDto = CreateAuctionDto.builder()
                .sellerId(1L)
                .build();

        int initialAuctionCount = auctionRepository.findAll().size();

        auctionService.createAuction(createAuctionDto);

        List<Auction> auctions = auctionRepository.findAll();

        assertEquals(initialAuctionCount + 1, auctions.size(), "One auction should be added");

        Auction createdAuction = auctions.get(auctions.size() - 1);
        assertEquals(createAuctionDto.getSellerId(), createdAuction.getSellerId(), "Seller IDs should match");
    }

    @Test
    void testCreateAuctionWithNullSellerId() {
        CreateAuctionDto createAuctionDto = CreateAuctionDto.builder().build();
        assertThrows(SellerIdIsNullException.class, () -> auctionService.createAuction(createAuctionDto));
    }

}
