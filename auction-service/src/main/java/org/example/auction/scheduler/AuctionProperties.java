package org.example.auction.scheduler;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Auction timing settings under {@code auction}.
 *
 * @param duration how long an auction runs after its start time before it becomes eligible to finish
 */
@ConfigurationProperties("auction")
public record AuctionProperties(@DefaultValue("PT6H") Duration duration) {}
