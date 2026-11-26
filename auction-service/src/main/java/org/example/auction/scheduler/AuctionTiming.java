package org.example.auction.scheduler;

import java.time.Duration;
import java.time.Instant;

/** When an auction in progress becomes eligible to finish. */
public final class AuctionTiming {

    private AuctionTiming() {}

    /**
     * Tells whether an auction that started at {@code startTime} has run for at least {@code duration}.
     *
     * @param startTime the auction's start time
     * @param duration the configured auction duration
     * @param now the current time
     * @return true once {@code now} is at or after {@code startTime} plus {@code duration}
     */
    public static boolean isDue(Instant startTime, Duration duration, Instant now) {
        return !now.isBefore(startTime.plus(duration));
    }
}
