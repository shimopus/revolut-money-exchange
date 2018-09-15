INSERT INTO currency (id, name, abbr)
VALUES
  (1, 'US Dollar','USD'),
  (2, 'Euro', 'EUR'),
  (3, 'Russian Rubles', 'RUB');

INSERT INTO transaction_status (id, name)
VALUES
       (1, 'Planned'),
       (2, 'Processing'),
       (3, 'Failed'),
       (4, 'Succeed');

INSERT INTO bank_account (owner_name, balance, blocked_amount, currency_id)
VALUES
  ('Sergey Babinskiy', 1000.5, 0, 3),
  ('Nikolay Storonsky', 1000.5, 0, 2),
  ('Vlad Yatsenko', 1000.5, 0, 1);