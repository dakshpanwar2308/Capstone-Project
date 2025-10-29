CREATE TABLE theatres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(50)
);

CREATE TABLE shows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    theatre_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    screen VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    CONSTRAINT fk_shows_theatre FOREIGN KEY (theatre_id) REFERENCES theatres (id)
);
