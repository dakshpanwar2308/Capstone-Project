CREATE TABLE movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    language VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    rating DOUBLE NOT NULL,
    poster_url VARCHAR(512),
    synopsis TEXT,
    release_date DATE NOT NULL
);
