package org.example.apigateway.service;

import java.time.Instant;
import java.util.List;

import org.example.apigateway.dto.auction.AuctionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuctionServiceTest extends AbstractConfiguration {

    private static final String AUCTIONS_PATH = "/api/v1/auctions";

    @Autowired
    AuctionService auctionService;

    @Test
    void shouldReturnListWithAuctions() {
        // given: auction-service answers with the JSON its AuctionController produces
        stubFor(get(urlEqualTo(AUCTIONS_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("auction-service/auctions.json")));

        // when
        List<AuctionDto> auctions = auctionService.findAllAuction();

        // then
        verify(getRequestedFor(urlEqualTo(AUCTIONS_PATH)));
        assertEquals(3, auctions.size());

        AuctionDto planned = auctions.get(0);
        assertEquals(101L, planned.getAuctionId());
        assertEquals("PLANNED", planned.getAuctionState());
        assertNull(planned.getAuctionDate());
        assertNull(planned.getItemDto().getId());
        assertEquals(7L, planned.getItemDto().getSeller());
        assertNull(planned.getPriceDto().getBuyer());

        AuctionDto inProgress = auctions.get(1);
        assertEquals("IN_PROGRESS", inProgress.getAuctionState());
        assertEquals(31L, inProgress.getItemDto().getId());
        assertEquals(190.0, inProgress.getPriceDto().getCurrentPrice());
        assertEquals(12L, inProgress.getPriceDto().getBuyer());

        AuctionDto finished = auctions.get(2);
        assertEquals("FINISHED", finished.getAuctionState());
        assertEquals(625.0, finished.getPriceDto().getFinalPrice());
        assertFalse(finished.getIsPayed());
        assertEquals(
                Instant.parse("2026-11-22T18:00:00Z"), finished.getAuctionDate().toInstant());
    }
}
