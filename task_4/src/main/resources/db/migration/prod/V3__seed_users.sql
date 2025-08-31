INSERT INTO app_data.users(username) VALUES
  ('charlie'), ('diana')
ON CONFLICT DO NOTHING;