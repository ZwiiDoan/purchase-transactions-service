create table if not exists purchase_transaction
(
    description      varchar(50),
    transaction_date DATE,
    purchase_amount  DECIMAL(10000, 2),
    transaction_id   UUID
);