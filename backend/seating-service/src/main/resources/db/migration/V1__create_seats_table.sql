CREATE TABLE seats (
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
