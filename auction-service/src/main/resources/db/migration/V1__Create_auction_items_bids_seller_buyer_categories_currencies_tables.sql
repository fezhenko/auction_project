CREATE TABLE IF NOT EXISTS auctions
(
    auction_id    BIGSERIAL NOT NULL UNIQUE,
    auction_date  TIMESTAMP NOT NULL,
    auction_state VARCHAR   NOT NULL CHECK (auction_state in ('PLANNED', 'IN_PROGRESS', 'FINISHED', 'CANCELED')),
    seller_id     BIGSERIAL NOT NULL,
    item_id       BIGSERIAL NOT NULL UNIQUE,
    start_price   INTEGER            DEFAULT 0,
    current_price INTEGER,
    final_price   INTEGER,
    buyer_id      BIGSERIAL NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (auction_id)
);

CREATE TABLE IF NOT EXISTS bids
(
    bid_id     BIGSERIAL NOT NULL UNIQUE,
    bid_amount INTEGER   NOT NULL,
    buyer_id   BIGSERIAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (bid_id)
);

CREATE TABLE IF NOT EXISTS buyers
(
    id         BIGSERIAL NOT NULL UNIQUE,
    bid_id     BIGSERIAL NOT NULL,
    auction_id BIGSERIAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    FOREIGN KEY (bid_id) REFERENCES bids (bid_id),
    FOREIGN KEY (auction_id) REFERENCES auctions (auction_id)
);

CREATE TABLE IF NOT EXISTS sellers
(
    id         BIGSERIAL NOT NULL UNIQUE,
    auction_id BIGSERIAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    FOREIGN KEY (auction_id) REFERENCES auctions (auction_id)
);

CREATE TABLE IF NOT EXISTS chat
(
    id         BIGSERIAL    NOT NULL UNIQUE,
    message    VARCHAR(500) NULL,
    buyer_id   BIGSERIAL    NOT NULL,
    seller_id  BIGSERIAL    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    FOREIGN KEY (buyer_id) REFERENCES buyers (id),
    FOREIGN KEY (seller_id) REFERENCES sellers (id)
);
