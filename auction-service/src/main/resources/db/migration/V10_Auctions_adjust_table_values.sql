ALTER TABLE auctions
    alter column auction_date drop not null,
    alter column auction_date set default null,
    alter column auction_state drop not null,
    alter column auction_state set default 'PLANNED',
    alter column item_id drop not null,
    alter column item_id set default null,
    alter column current_price set default null,
    alter column final_price set default null,
    alter column buyer_id drop not null,
    alter column buyer_id set default null;