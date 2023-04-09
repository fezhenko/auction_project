alter table auctions
    add column if not exists is_payed BOOLEAN default false;

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