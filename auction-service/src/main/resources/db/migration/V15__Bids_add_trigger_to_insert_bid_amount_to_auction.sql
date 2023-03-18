create function update_bids_for_auction()
    returns trigger
as
$$
begin
    update buyers set bid_id = new.bid_id where buyers.id = new.buyer_id;
    update auctions a
    set buyer_id      = new.buyer_id,
        current_price = new.bid_amount
    from buyers b
    where b.auction_id = a.auction_id
      and b.id = new.buyer_id
      and current_price < new.bid_amount;
    return new;
end;
$$ language plpgsql;
create trigger do_bid_set_current_price
    after insert or update of bid_amount
    on bids
    for each row
execute procedure update_bids_for_auction();