-- 1. userCredentials: stores login credentials
CREATE TABLE "userCredentials" (
                                   id SERIAL PRIMARY KEY,
                                   username VARCHAR(255) NOT NULL UNIQUE,
                                   password VARCHAR(255) NOT NULL
);

-- 2. user: references userCredentials and stores additional user info
CREATE TABLE "user" (
                        id SERIAL PRIMARY KEY,
                        fk_user_id INT NOT NULL,
                        in_game_name VARCHAR(255),
                        bio TEXT,
                        image TEXT,
                        coins INT DEFAULT 20,
                        wins INT DEFAULT 0,
                        losses INT DEFAULT 0,
                        mmr INT DEFAULT 1000,
                        FOREIGN KEY (fk_user_id) REFERENCES "userCredentials"(id)
);

-- 3. MonsterCards: stores card details
CREATE TABLE "MonsterCards" (
                                c_uuid VARCHAR(36) PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                damage INT NOT NULL,
                                element INT NOT NULL
);

-- 4. packages: stores card packages referencing MonsterCards
CREATE TABLE "packages" (
                            package_id SERIAL PRIMARY KEY,
                            card_1_id VARCHAR(36) NOT NULL,
                            card_2_id VARCHAR(36) NOT NULL,
                            card_3_id VARCHAR(36) NOT NULL,
                            card_4_id VARCHAR(36) NOT NULL,
                            card_5_id VARCHAR(36) NOT NULL,
                            FOREIGN KEY (card_1_id) REFERENCES "MonsterCards"(c_uuid),
                            FOREIGN KEY (card_2_id) REFERENCES "MonsterCards"(c_uuid),
                            FOREIGN KEY (card_3_id) REFERENCES "MonsterCards"(c_uuid),
                            FOREIGN KEY (card_4_id) REFERENCES "MonsterCards"(c_uuid),
                            FOREIGN KEY (card_5_id) REFERENCES "MonsterCards"(c_uuid)
);

-- 5. acquired_cards: links users to cards they own
CREATE TABLE "acquired_cards" (
                                  fk_acquired_cards_user_id INT NOT NULL,
                                  fk_acquired_cards_card_id VARCHAR(36) NOT NULL,
                                  FOREIGN KEY (fk_acquired_cards_user_id) REFERENCES "userCredentials"(id),
                                  FOREIGN KEY (fk_acquired_cards_card_id) REFERENCES "MonsterCards"(c_uuid),
                                  PRIMARY KEY (fk_acquired_cards_user_id, fk_acquired_cards_card_id)
);

-- 6. decks: which cards are currently in a user's deck
CREATE TABLE "decks" (
                         fk_decks_user_id INT NOT NULL,
                         fk_decks_card_id VARCHAR(36) NOT NULL,
                         FOREIGN KEY (fk_decks_user_id) REFERENCES "userCredentials"(id),
                         FOREIGN KEY (fk_decks_card_id) REFERENCES "MonsterCards"(c_uuid),
                         PRIMARY KEY (fk_decks_user_id, fk_decks_card_id)
);

-- 7. trades: active trades referencing userCredentials and MonsterCards
CREATE TABLE "trades" (
                          trade_id VARCHAR(50) PRIMARY KEY,
                          card_to_trade_id VARCHAR(36) NOT NULL,
                          type VARCHAR(50),
                          minimum_damage INT NOT NULL,
                          created_by INT NOT NULL,
                          FOREIGN KEY (card_to_trade_id) REFERENCES "MonsterCards"(c_uuid),
                          FOREIGN KEY (created_by) REFERENCES "userCredentials"(id)
);
