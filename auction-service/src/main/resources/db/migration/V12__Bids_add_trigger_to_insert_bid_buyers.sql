alter table bids
    alter column buyer_id drop default;

create function update_bids_for_buyer()
    returns trigger
as
$$
begin
    update buyers set bid_id = new.bid_id where buyers.id = new.buyer_id;
    return new;
end;
$$ language plpgsql;
create trigger update_bids_for_buyer
    after insert or update of bid_amount
    on bids
    for each row
execute procedure update_bids_for_buyer();