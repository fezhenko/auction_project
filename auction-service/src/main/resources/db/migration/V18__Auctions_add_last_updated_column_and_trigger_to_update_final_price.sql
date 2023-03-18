alter table auctions
    add column last_updated timestamp default null;

create function update_auction_last_updated_time()
    returns trigger
as
$$
BEGIN
    IF new.last_updated > new.created_at THEN
        RETURN NEW;
    ELSE
        RETURN NULL;
    END IF;
END
$$ language plpgsql;
create trigger update_auction_last_updated_time
    after update
    on auctions
    for each row
execute procedure update_auction_last_updated_time();
