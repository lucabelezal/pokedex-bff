CREATE TABLE IF NOT EXISTS pokemon (
    id BIGINT PRIMARY KEY,
    identifier VARCHAR(255),
    species_id BIGINT,
    height INT,
    weight INT,
    base_experience INT,
    order_number INT,
    is_default BOOLEAN
);
