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
);

CREATE TABLE transaction_status (
  id INT PRIMARY KEY,
  name VARCHAR(30)
);

CREATE TABLE transaction (
  id IDENTITY,
  from_account_id BIGINT NOT NULL,
  to_account_id BIGINT NOT NULL,
  amount DECIMAL(19,4) NOT NULL,
  currency_id INT NOT NULL,
  creation_date TIMESTAMP NOT NULL,
  update_date TIMESTAMP,
  status_id INT NOT NULL,

  FOREIGN KEY(from_account_id) REFERENCES bank_account(id),
  FOREIGN KEY(to_account_id) REFERENCES bank_account(id),
  FOREIGN KEY(currency_id) REFERENCES currency(id),
  FOREIGN KEY(status_id) REFERENCES transaction_status(id)
)