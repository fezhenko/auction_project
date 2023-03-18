Alter table sellers
    alter column auction_id set default null;

alter table sellers
    alter column auction_id drop not null;