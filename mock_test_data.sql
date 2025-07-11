-- mock_test_data.sql: 모의투자 서비스 API 테스트용 Mock 데이터
-- docker cp mock_test_data.sql postgres:/mock_test_data.sql
-- docker exec -it postgres bash
-- psql -U sa -d database -f /mock_test_data.sql


-- 1. User
INSERT INTO users (username, email, seed_money, cash, join_at, created_at, updated_at)
VALUES
  ('alice', 'alice@example.com', 10000000, 10000000, '2024-07-09 09:00:00', now(), now()),
  ('bob', 'bob@example.com', 5000000, 5000000, '2024-07-09 09:05:00', now(), now());
