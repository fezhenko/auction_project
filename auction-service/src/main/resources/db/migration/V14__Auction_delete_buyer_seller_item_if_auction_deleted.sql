create function delete_buyer_seller_item_if_auction_deleted() returns trigger as
$$
BEGIN
    delete from buyers where auction_id = old.auction_id;
    delete from sellers where auction_id = old.auction_id;
    return old;
END
$$ language plpgsql;
create trigger delete_buyer_seller_item_if_auction_deleted
    before delete
    on auctions
    for each row
execute procedure delete_buyer_seller_item_if_auction_deleted();
