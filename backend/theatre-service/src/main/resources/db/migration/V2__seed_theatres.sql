INSERT INTO theatres (name, city, address, contact_number) VALUES
    ('Central Plaza Cinema', 'New York', '123 Main St, New York, NY', '+1-212-123-4567'),
    ('Silver Screen Multiplex', 'San Francisco', '456 Market St, San Francisco, CA', '+1-415-555-0101'),
    ('Aurora Theatre', 'Chicago', '789 Lakeshore Dr, Chicago, IL', '+1-312-555-2222');

INSERT INTO shows (theatre_id, movie_id, start_time, screen, base_price) VALUES
    (1, 1, '2024-04-12 18:30:00', 'Screen 1', 350.00),
    (1, 2, '2024-04-12 21:30:00', 'Screen 2', 320.00),
    (2, 3, '2024-04-13 17:00:00', 'Screen 3', 280.00),
    (2, 4, '2024-04-13 20:00:00', 'Screen 1', 300.00),
    (3, 5, '2024-04-14 19:00:00', 'Screen 1', 250.00);
