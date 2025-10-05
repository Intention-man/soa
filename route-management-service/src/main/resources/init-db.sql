-- ==========================
-- Таблица координат
-- ==========================
CREATE TABLE coordinates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    x INTEGER NOT NULL,
    y BIGINT NOT NULL
);

-- ==========================
-- Таблица начальных локаций
-- ==========================
CREATE TABLE from_locations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    x INTEGER NOT NULL,
    y BIGINT NOT NULL
);

-- ==========================
-- Таблица конечных локаций
-- ==========================
CREATE TABLE to_locations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    x BIGINT NOT NULL,
    y INTEGER NOT NULL
);

-- ==========================
-- Таблица маршрутов
-- ==========================
CREATE TABLE routes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    coordinates_id BIGINT NOT NULL,
    from_location_id BIGINT NOT NULL,
    to_location_id BIGINT NOT NULL,
    distance DOUBLE PRECISION NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coordinates FOREIGN KEY (coordinates_id) REFERENCES coordinates(id),
    CONSTRAINT fk_from_location FOREIGN KEY (from_location_id) REFERENCES from_locations(id),
    CONSTRAINT fk_to_location FOREIGN KEY (to_location_id) REFERENCES to_locations(id)
);

-- ==========================
-- Тестовые данные
-- ==========================

INSERT INTO coordinates (x, y) VALUES
(12, 46),
(22, 33),
(40, 50);

INSERT INTO from_locations (name, x, y) VALUES
('Amsterdam', 5, 52),
('Rotterdam', 4, 52),
('The Hague', 4, 52);

INSERT INTO to_locations (name, x, y) VALUES
('Utrecht', 5, 52),
('Eindhoven', 5, 51),
('Maastricht', 6, 50);

INSERT INTO routes (name, coordinates_id, from_location_id, to_location_id, distance)
VALUES
('Route 1', 1, 1, 2, 60.5),
('Route 2', 2, 2, 3, 120.0),
('Route 3', 3, 1, 3, 180.2);
