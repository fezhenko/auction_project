ALTER TABLE auctions
    ALTER COLUMN item_id DROP NOT NULL,
    ALTER COLUMN buyer_id DROP NOT NULL;