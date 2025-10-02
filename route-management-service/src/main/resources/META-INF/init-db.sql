CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    coord_x DOUBLE PRECISION NOT NULL,
    coord_y DOUBLE PRECISION NOT NULL,
    from_x DOUBLE PRECISION NOT NULL,
    from_y DOUBLE PRECISION NOT NULL,
    from_name VARCHAR(255) NOT NULL,
    to_x DOUBLE PRECISION NOT NULL,
    to_y DOUBLE PRECISION NOT NULL,
    to_name VARCHAR(255) NOT NULL,
    distance DOUBLE PRECISION NOT NULL CHECK (distance > 1)
);

-- Добавим тестовые строки
INSERT INTO routes (name, coord_x, coord_y, from_x, from_y, from_name, to_x, to_y, to_name, distance)
VALUES
('Route A', 10, 20, 1, 2, 'Start A', 3, 4, 'End A', 123.45),
('Route B', 50, 60, 5, 6, 'Start B', 7, 8, 'End B', 250.0),
('Route C', -10, 15, 0, 0, 'Origin', 20, 30, 'Destination', 500.5);
