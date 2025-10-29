-- I-Cinema consolidated DDL script

-- Movies Service
CREATE TABLE IF NOT EXISTS movies (
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

-- Theatre Service
CREATE TABLE IF NOT EXISTS theatres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS shows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    theatre_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    screen VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    CONSTRAINT fk_shows_theatre FOREIGN KEY (theatre_id) REFERENCES theatres (id)
);

-- Seating Service
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    show_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    price DOUBLE NOT NULL,
    hold_token VARCHAR(100),
    hold_expires_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT uq_seat UNIQUE (show_id, seat_number)
);

-- Booking Service
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    show_id BIGINT NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_email VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30),
    total_amount DOUBLE NOT NULL,
    hold_token VARCHAR(120),
    payment_reference VARCHAR(120),
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS booking_seats (
    booking_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

-- Payment Service
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider_reference VARCHAR(120),
    processed_at DATETIME,
    receipt_email VARCHAR(150)
);
