CREATE TABLE bookings (
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

CREATE TABLE booking_seats (
    booking_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);
