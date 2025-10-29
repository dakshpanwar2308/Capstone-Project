ALTER TABLE movies
    ADD COLUMN censor_rating VARCHAR(10) NOT NULL DEFAULT 'U/A';

ALTER TABLE movies
    ADD COLUMN rating_count BIGINT NOT NULL DEFAULT 0;

UPDATE movies SET censor_rating = 'U/A' WHERE censor_rating IS NULL;
UPDATE movies SET rating_count = 50 WHERE rating_count = 0 AND rating >= 4.0;
UPDATE movies SET rating_count = 35 WHERE rating_count = 0 AND rating >= 3.5 AND rating < 4.0;
UPDATE movies SET rating_count = 20 WHERE rating_count = 0;
