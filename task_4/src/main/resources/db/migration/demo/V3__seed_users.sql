INSERT INTO app_demo.users (username) VALUES
  ('charlie'), ('diana')
ON CONFLICT DO NOTHING;
