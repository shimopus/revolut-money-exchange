INSERT INTO currency (id, name, abbr)
VALUES
  (1, 'US Dollar','USD'),
  (2, 'Euro', 'EUR'),
  (3, 'Russian Rubles', 'RUB');

INSERT INTO bank_account (owner_name, balance, blocked_amount, currency_id)
VALUES
  ('Sergey Babinskiy', 1000.5, 0, 3),
  ('Nikolay Storonsky', 1000.5, 0, 2),
  ('Vlad Yatsenko', 1000.5, 0, 1);