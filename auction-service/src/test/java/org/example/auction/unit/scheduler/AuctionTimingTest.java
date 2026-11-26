package org.example.auction.unit.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.example.auction.scheduler.AuctionProperties;
import org.example.auction.scheduler.AuctionTiming;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/** An auction in progress becomes eligible to finish once its start time plus the auction duration is reached. */
class AuctionTimingTest {

    private static final Instant START = Instant.parse("2026-11-26T06:00:00Z");

    @Test
    void durationDefaultsToSixHours() {
        assertThat(bind(Map.of()).duration()).isEqualTo(Duration.ofHours(6));
    }

    @ParameterizedTest(name = "{0} after the start: due = {1}")
    @CsvSource({"PT5H, false", "PT5H59M59.999S, false", "PT6H, true", "PT6H0.001S, true"})
    void defaultDurationIsDueSixHoursAfterTheStart(Duration elapsed, boolean due) {
        Duration duration = bind(Map.of()).duration();

        assertThat(AuctionTiming.isDue(START, duration, START.plus(elapsed))).isEqualTo(due);
    }

    @ParameterizedTest(name = "{0} after the start: due = {1}")
    @CsvSource({"PT59.999S, false", "PT1M, true", "PT1M0.001S, true", "PT2M, true"})
    void configuredDurationOfOneMinuteIsDueOneMinuteAfterTheStart(Duration elapsed, boolean due) {
        Duration duration = bind(Map.of("auction.duration", "PT1M")).duration();

        assertThat(duration).isEqualTo(Duration.ofMinutes(1));
        assertThat(AuctionTiming.isDue(START, duration, START.plus(elapsed))).isEqualTo(due);
    }

    private static AuctionProperties bind(Map<String, String> properties) {
        return new Binder(new MapConfigurationPropertySource(properties))
                .bindOrCreate("auction", AuctionProperties.class);
    }
}
