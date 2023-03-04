CREATE TABLE IF NOT EXISTS payments
(
    payment_id BIGSERIAL NOT NULL UNIQUE,
    user_id BIGSERIAL NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    expiration_date VARCHAR(5) NOT NULL,
    amount INTEGER NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (payment_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);


INSERT INTO payments (user_id, card_number, expiration_date, amount)
VALUES (1, '1234567891234567', '01/25', '1000');
INSERT INTO payments (user_id, card_number, expiration_date, amount)
VALUES (2, '9876543212345678', '02/26', '100');