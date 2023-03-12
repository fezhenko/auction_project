ALTER TABLE bids
    ADD buyer_id BIGSERIAL not null,
    ADD FOREIGN KEY (buyer_id) REFERENCES buyers (id);