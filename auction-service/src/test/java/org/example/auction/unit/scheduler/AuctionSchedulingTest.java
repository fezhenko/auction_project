package org.example.auction.unit.scheduler;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import org.example.auction.converter.AuctionConverter;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.BuyerRepository;
import org.example.auction.repository.SellerRepository;
import org.example.auction.scheduler.AuctionProperties;
import org.example.auction.scheduler.AuctionStatus;
import org.example.auction.scheduler.client.ItemClient;
import org.example.auction.scheduler.client.UserClient;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/** The start and finish jobs take the current time from the Clock bean and the duration from auction.duration. */
@ExtendWith(MockitoExtension.class)
class AuctionSchedulingTest {

    private static final Instant NOW = Instant.parse("2026-11-26T12:00:00Z");
    private static final long DUE_ID = 1L;
    private static final long NOT_DUE_ID = 2L;

    private final TimeZone originalTimeZone = TimeZone.getDefault();

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionConverter auctionConverter;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private ItemClient itemClient;

    @Mock
    private UserClient userClient;

    @AfterEach
    void restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    void startJobPassesTheClockInstantToTheRepository() {
        auctionService(Duration.ofHours(6)).startAuction();

        verify(auctionRepository).startDueAuctions(NOW);
        verifyNoMoreInteractions(auctionRepository);
    }

    @Test
    void finishJobUsesTheConfiguredDuration() {
        doReturn(List.of(
                        inProgress(DUE_ID, NOW.minus(Duration.ofMinutes(2))),
                        inProgress(NOT_DUE_ID, NOW.minus(Duration.ofSeconds(30)))))
                .when(auctionRepository)
                .findAll();

        auctionService(Duration.ofMinutes(1)).finishAuction();

        verify(auctionRepository).updateAuctionStateBySchedule(AuctionStatus.FINISHED, DUE_ID);
        verify(auctionRepository, never()).updateAuctionStateBySchedule(eq(AuctionStatus.FINISHED), eq(NOT_DUE_ID));
    }

    @ParameterizedTest
    @ValueSource(strings = {"UTC", "Asia/Tokyo"})
    void hostTimeZoneDoesNotChangeWhichAuctionsAreDue(String zone) {
        TimeZone.setDefault(TimeZone.getTimeZone(zone));
        doReturn(List.of(
                        inProgress(DUE_ID, NOW.minus(Duration.ofHours(6))),
                        inProgress(NOT_DUE_ID, NOW.minus(Duration.ofHours(6)).plusMillis(1))))
                .when(auctionRepository)
                .findAll();

        auctionService(Duration.ofHours(6)).finishAuction();

        verify(auctionRepository).updateAuctionStateBySchedule(AuctionStatus.FINISHED, DUE_ID);
        verify(auctionRepository, never()).updateAuctionStateBySchedule(eq(AuctionStatus.FINISHED), eq(NOT_DUE_ID));
    }

    private AuctionService auctionService(Duration duration) {
        return new AuctionService(
                auctionRepository,
                auctionConverter,
                sellerRepository,
                buyerRepository,
                itemClient,
                userClient,
                Clock.fixed(NOW, ZoneOffset.UTC),
                new AuctionProperties(duration));
    }

    private static Auction inProgress(long auctionId, Instant startTime) {
        return Auction.builder()
                .auctionId(auctionId)
                .auctionDate(Timestamp.from(startTime))
                .auctionState(AuctionStatus.IN_PROGRESS.name())
                .itemId(auctionId)
                .currentPrice(100d)
                .build();
    }
}
