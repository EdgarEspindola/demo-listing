INSERT INTO customer(name,email) VALUES
('Alice','alice@acme.com'), ('Bob','bob@acme.com'), ('Carol','carol@acme.com');

INSERT INTO payment(method,status) VALUES ('CARD','PAID'), ('CASH','PENDING');

INSERT INTO orders(created_at,total_amount,status,customer_id,payment_id)
VALUES
(now() - interval '1 minute', 120.50,'PAID', 1, 1),
(now() - interval '2 minute',  90.00,'PENDING', 1, 2),
(now() - interval '3 minute',  15.00,'PAID', 2, 1),
(now() - interval '4 minute', 230.00,'PAID', 3, 1);
