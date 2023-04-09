alter table auctions
    alter column buyer_id set default null,
    alter column seller_id drop default;