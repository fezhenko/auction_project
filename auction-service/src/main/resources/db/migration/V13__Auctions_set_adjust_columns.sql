alter table auctions
    alter column auction_date drop not null;

alter table auctions
    alter column auction_state drop not null;

alter table auctions
    alter column auction_state set default 'PLANNED';

alter table auctions
    alter column item_id set default null;

create function seller_insert_into_auction()
    returns trigger
as
$$
begin
    insert into auctions (auction_date, auction_state, seller_id, item_id, start_price, current_price, final_price,
                          buyer_id, created_at, minimal_bid)
    values (default, default, new.id, default, default, default, default, default, default, default);
    return new;
end;
$$ language plpgsql;

create trigger seller_insert_into_auction_test
    after insert
    on sellers
    for each row
execute procedure seller_insert_into_auction();