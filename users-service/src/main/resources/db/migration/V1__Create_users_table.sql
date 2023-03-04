CREATE TABLE IF NOT EXISTS users
    (
        user_id BIGSERIAL NOT NULL UNIQUE,
        firstname VARCHAR(255),
        lastname VARCHAR(255),
        email VARCHAR(50) NOT NULL,
        password VARCHAR NOT NULL,
        role VARCHAR NOT NULL DEFAULT 'USER' CHECK (role in ('ADMIN', 'USER')),
        balance INTEGER NOT NULL DEFAULT 0,
        phone_number INTEGER,
        created_at TIMESTAMP NOT NULL DEFAULT now(),
        PRIMARY KEY (user_id)
    );


INSERT INTO users (email, password, role)
VALUES ('test@admin.com', '$2a$10$cWdjJViYxnr6BL4BujyZv.6jZgDhf9t3o7N0ww5Nn3SdBcoq.hXPG', 'ADMIN');
INSERT INTO users (email, password, role)
VALUES ('test@user.com', '$2a$10$cWdjJViYxnr6BL4BujyZv.6jZgDhf9t3o7N0ww5Nn3SdBcoq.hXPG', 'USER');
