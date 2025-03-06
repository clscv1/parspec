CREATE TABLE IF NOT EXISTS orders (
    orderId CHAR(128) PRIMARY KEY,
    userId CHAR(128) NOT NULL,
    itemIds TEXT NOT NULL,
    totalAmount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20)
);
