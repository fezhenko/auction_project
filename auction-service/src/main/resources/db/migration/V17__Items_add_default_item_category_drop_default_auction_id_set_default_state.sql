alter table items
    alter column item_category set default 1;

alter table items
    alter column auction_id drop default;

alter table auctions
    alter column seller_id drop default;

-- create trigger to update item_id, start_price, minimal_bid after creating an item
create function update_item_id_for_auction()
    returns trigger
as
$$
begin
    update auctions
    set item_id = new.id, start_price = new.price, minimal_bid = (new.price * 0.05)
    where auctions.auction_id = new.auction_id
      and new.item_state = 'ON_SELL'
      and auctions.auction_state = 'PLANNED';
    return new;
end;
$$ language plpgsql;
create trigger insert_item_id_to_auction
    after insert
    on items
    for each row
execute procedure update_item_id_for_auction();

--create function ad trigger to insert auction_id into sellers after creating new auction
create function auction_to_sellers()
    returns trigger
as
$$
begin
    update sellers set auction_id = new.auction_id where id = new.seller_id;
    return new;
end;
$$ language plpgsql;
create trigger auction_id_to_sellers
    after insert on auctions
    for each row
execute procedure auction_to_sellers();