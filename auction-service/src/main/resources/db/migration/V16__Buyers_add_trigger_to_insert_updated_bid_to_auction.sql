create function insert_new_bid_to_current_price()
    returns trigger
as
$$
begin
    update auctions a
    set buyer_id      = new.id,
        current_price = b.bid_amount
    from bids b
    where b.bid_id = new.bid_id
      and a.auction_id = new.auction_id
      and a.start_price <= b.bid_amount
      and a.current_price < b.bid_amount
      and (b.bid_amount - a.current_price) >= a.minimal_bid
      and a.auction_state in ('IN_PROGRESS');
    return new;
end;
$$ language plpgsql;
create trigger insert_new_bid_to_current_price
    after insert or update of bid_id
    on buyers
    for each row
execute procedure insert_new_bid_to_current_price();