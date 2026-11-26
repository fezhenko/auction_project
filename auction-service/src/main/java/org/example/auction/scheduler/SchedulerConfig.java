package org.example.auction.scheduler;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(AuctionProperties.class)
public class SchedulerConfig {

    /** The only time source for scheduling decisions, so tests can replace it with a fixed clock. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
