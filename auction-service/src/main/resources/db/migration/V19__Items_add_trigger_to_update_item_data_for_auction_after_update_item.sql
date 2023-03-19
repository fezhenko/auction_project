create function update_auctions_if_item_updated()
    returns trigger
as
$$
begin
    update auctions
    set item_id     = new.id,
        start_price = new.price,
        minimal_bid = (new.price * 0.05)
    where auctions.auction_id = new.auction_id
      and item_state = new.item_state;
    return new;
end;
$$ language plpgsql;
create trigger update_auctions_if_item_updated
    after update
    on items
    for each row
execute procedure update_auctions_if_item_updated();