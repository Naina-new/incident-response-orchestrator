CREATE TABLE incidents (
                           id SERIAL PRIMARY KEY,
                           title VARCHAR(255),
                           description TEXT,
                           status VARCHAR(50),
                           priority VARCHAR(50),
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP
);