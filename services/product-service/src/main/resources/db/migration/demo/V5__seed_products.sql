-- Демоданные (ожидаем, что users(id=1,2) уже есть из прошлых сидов)
insert into products (account_number, balance, type, user_id) values
  ('ACC-0001', 100.00, 'ACCOUNT', 1),
  ('CARD-0001',   0.00, 'CARD',    1),
  ('ACC-0002', 250.50, 'ACCOUNT', 2)
on conflict (account_number) do nothing;
