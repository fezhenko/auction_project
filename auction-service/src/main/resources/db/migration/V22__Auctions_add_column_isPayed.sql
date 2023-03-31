alter table auctions
    add column if not exists is_payed BOOLEAN default false;