DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS facilities CASCADE;
DROP TABLE IF EXISTS clients CASCADE;

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_clients_name CHECK (length(trim(full_name)) >= 2),
    CONSTRAINT chk_clients_phone CHECK (phone ~ '^\+?[0-9]{10,15}$')
);

CREATE TABLE facilities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    sport_type VARCHAR(50) NOT NULL,
    price_per_hour NUMERIC(10, 2) NOT NULL,

    CONSTRAINT chk_facilities_price CHECK (price_per_hour > 0),
    CONSTRAINT chk_facilities_name CHECK (length(trim(name)) >= 2)
);

CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    facility_id INT NOT NULL REFERENCES facilities(id) ON DELETE RESTRICT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_bookings_time CHECK (end_time > start_time),
    CONSTRAINT chk_bookings_status CHECK (status IN ('CREATED', 'CONFIRMED', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_bookings_client ON bookings(client_id);
CREATE INDEX idx_bookings_facility ON bookings(facility_id);
CREATE INDEX idx_bookings_start ON bookings(start_time);
CREATE INDEX idx_bookings_status ON bookings(status);

INSERT INTO clients (full_name, phone, email) VALUES
('Джон Смит', '+79999060726', 'john@example.com'),
('Иван Петров', '+79991112233', 'ivan@example.com'),
('Мария Сидорова', '+79994445566', NULL);

INSERT INTO facilities (name, sport_type, price_per_hour) VALUES
('Поле №1', 'Футбол', 1500.00),
('Корт №1', 'Теннис', 800.00),
('Бассейн, дорожка 1', 'Плавание', 500.00);