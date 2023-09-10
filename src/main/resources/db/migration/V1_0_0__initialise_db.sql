CREATE TABLE IF NOT EXISTS purchase_transaction
(
    description      VARCHAR(50),
    transaction_date DATE,
    purchase_amount  NUMERIC(1000, 2),
    transaction_id   UUID
);

CREATE TABLE IF NOT EXISTS exchange_rate
(
    currency       VARCHAR(255),
    effective_date DATE,
    exchange_rate  NUMERIC(100, 10),
    PRIMARY KEY (currency, effective_date)
);