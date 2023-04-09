CREATE TABLE IF NOT EXISTS currencies
(
    id       BIGSERIAL NOT NULL UNIQUE,
    currency json      NOT NULL,
    PRIMARY KEY (id)
);

insert into currencies (currency)
values ('{
  "currency": "EUR",
  "currencyDescription": "Euro"
}'::json);
insert into currencies (currency)
values ('{
  "currency": "BYN",
  "currencyDescription": "New Belarusian Ruble"
}'::json);
insert into currencies (currency)
values ('{
  "currency": "PLN",
  "currencyDescription": "Polish Zloty"
}'::json);
insert into currencies (currency)
values ('{
  "currency": "GBP",
  "currencyDescription": "British Pound Sterling"
}'::json);
insert into currencies (currency)
values ('{
  "currency": "USD",
  "currencyDescription": "United States Dollar"
}'::json);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGSERIAL NOT NULL UNIQUE,
    name VARCHAR(300),
    PRIMARY KEY (id)
);

insert into categories (name)
values ('Other');

CREATE TABLE IF NOT EXISTS items
(
    id            BIGSERIAL NOT NULL UNIQUE,
    description   VARCHAR(300),
    price         INTEGER   NOT NULL,
    currency_id   BIGSERIAL NOT NULL,
    item_category BIGSERIAL NOT NULL,
    item_state    VARCHAR   NOT NULL CHECK (item_state in ('SOLD', 'ON_SELL')) DEFAULT 'ON_SELL',
    created_at    TIMESTAMP NOT NULL                                           DEFAULT now(),
    owner_id      BIGSERIAL NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users (user_id),
    FOREIGN KEY (currency_id) REFERENCES currencies (id),
    FOREIGN KEY (item_category) REFERENCES categories (id)
);