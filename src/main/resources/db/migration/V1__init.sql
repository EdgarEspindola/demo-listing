CREATE TABLE customer(
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE
);

CREATE TABLE payment(
  id BIGSERIAL PRIMARY KEY,
  method TEXT NOT NULL,
  status TEXT NOT NULL
);

CREATE TABLE orders(
  id BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  total_amount NUMERIC(12,2) NOT NULL,
  status TEXT NOT NULL,
  customer_id BIGINT NOT NULL REFERENCES customer(id),
  payment_id BIGINT REFERENCES payment(id)
);

-- Índices recomendados (filtros + orden estable)
CREATE INDEX IF NOT EXISTS idx_orders_status_created_id
  ON orders (status, created_at DESC, id DESC);

-- Si buscas por nombre del cliente (q)
CREATE INDEX IF NOT EXISTS idx_customer_name_lower
  ON customer ((lower(name)));
