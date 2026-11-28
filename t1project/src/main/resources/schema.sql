CREATE TABLE IF NOT EXISTS matches (
    match_id VARCHAR(36) PRIMARY KEY,
    mode VARCHAR(32) NOT NULL,
    first_player VARCHAR(255) NOT NULL,
    second_player VARCHAR(255),
    first_secret_number VARCHAR(3),
    second_secret_number VARCHAR(3),
    current_turn VARCHAR(255) NOT NULL,
    winner VARCHAR(255),
    status VARCHAR(16) NOT NULL
);
