CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (account_id)
);

CREATE TABLE IF NOT EXISTS foods(
    food_id VARCHAR(255) NOT NULL,
    food_name VARCHAR(255) NOT NULL,
    food_type VARCHAR(255),
    PRIMARY KEY (food_id)
);

CREATE TABLE IF NOT EXISTS ingredients(
    food_id VARCHAR(255) NOT NULL,
    ingredient VARCHAR(255) NOT NULL,
    FOREIGN KEY (food_id) REFERENCES foods(food_id)
);