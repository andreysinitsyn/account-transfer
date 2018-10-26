DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL, Balance DECIMAL(19,2));

INSERT INTO Account (Balance) VALUES (100.00);
INSERT INTO Account (Balance) VALUES (200.00);
INSERT INTO Account (Balance) VALUES (400.00);
INSERT INTO Account (Balance) VALUES (500.00);
INSERT INTO Account (Balance) VALUES (300.00);

