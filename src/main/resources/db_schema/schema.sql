CREATE TABLE currency (
  id INT PRIMARY KEY,
  name VARCHAR(30),
  abbr VARCHAR(3)
);

CREATE TABLE bank_account (
  id IDENTITY,
  owner_name VARCHAR(256) NOT NULL,
  balance DECIMAL(19,4) NOT NULL,
  blocked_amount DECIMAL(19,4) NOT NULL,
  currency_id INT NOT NULL,
  FOREIGN KEY(currency_id) REFERENCES currency(id)
)