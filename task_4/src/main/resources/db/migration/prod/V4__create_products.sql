CREATE TABLE IF NOT EXISTS app_data.products (
    id              BIGSERIAL PRIMARY KEY,
    account_number  VARCHAR(32)  NOT NULL,
    balance         NUMERIC(19,2) NOT NULL DEFAULT 0,
    type            VARCHAR(16)   NOT NULL,
    user_id         BIGINT        NOT NULL,
    CONSTRAINT uk_products_account UNIQUE (account_number),
    CONSTRAINT fk_products_user
        FOREIGN KEY (user_id) REFERENCES app_data.users(id) ON DELETE CASCADE,
    CONSTRAINT chk_products_type CHECK (type IN ('ACCOUNT','CARD'))
);

CREATE INDEX IF NOT EXISTS idx_products_user ON app_data.products(user_id);
