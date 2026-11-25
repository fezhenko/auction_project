package org.example.auction.json;

import org.springframework.boot.SpringBootConfiguration;

/**
 * Ends the search for a configuration class at this package, so the {@code @JsonTest} classes here don't load
 * AuctionApplication. Its {@code @EnableFeignClients} registers Feign clients that a JSON slice can't create.
 */
@SpringBootConfiguration
public class JsonShapeTestConfiguration {}
