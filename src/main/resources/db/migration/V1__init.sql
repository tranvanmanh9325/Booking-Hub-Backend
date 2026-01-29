-- ============================================
-- Booking Hub Database Schema - Initialization
-- PostgreSQL Database Schema
-- This file is automatically executed on application startup
-- Note: If tables already exist, errors will be ignored due to continue-on-error=true
-- ============================================
-- ============================================
-- Users Table
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    avatar_url VARCHAR(500),
    reset_password_token VARCHAR(255),
    reset_password_token_expiry TIMESTAMP,
    role VARCHAR(50) DEFAULT 'USER',
    partner_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
-- Insert or Update the admin user
INSERT INTO users (
        email,
        password,
        full_name,
        role,
        created_at,
        updated_at
    )
VALUES (
        'manhtranlhp@gmail.com',
        '$2b$10$XoZf3JRtrwEyd4i.pbdIAeeHxQnDR1ZeGmBPtZssCywX095LVh1rO',
        'Manh Tran',
        'ADMIN',
        NOW(),
        NOW()
    ) ON CONFLICT (email) DO
UPDATE
SET role = 'ADMIN',
    password = '$2b$10$XoZf3JRtrwEyd4i.pbdIAeeHxQnDR1ZeGmBPtZssCywX095LVh1rO',
    full_name = 'Manh Tran';
-- Also ensure the original admin is set if it exists (optional)
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin';
-- ============================================
-- Hotels Table
-- ============================================
CREATE TABLE IF NOT EXISTS hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    star_rating INTEGER,
    description VARCHAR(2000),
    facilities VARCHAR(1000),
    phone_number VARCHAR(50),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hotels_city ON hotels(city);
CREATE INDEX IF NOT EXISTS idx_hotels_star_rating ON hotels(star_rating);
-- ============================================
-- Rooms Table
-- ============================================
CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_type VARCHAR(100) NOT NULL,
    max_guests INTEGER NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    amenities VARCHAR(1000),
    room_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rooms_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_rooms_hotel_id ON rooms(hotel_id);
CREATE INDEX IF NOT EXISTS idx_rooms_room_type ON rooms(room_type);
-- ============================================
-- Room Images Table
-- ============================================
CREATE TABLE IF NOT EXISTS room_images (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_images_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_room_images_room_id ON room_images(room_id);
CREATE INDEX IF NOT EXISTS idx_room_images_is_primary ON room_images(is_primary);
-- ============================================
-- Hotel Bookings Table
-- ============================================
CREATE TABLE IF NOT EXISTS hotel_bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INTEGER NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hotel_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_hotel_bookings_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT fk_hotel_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT chk_hotel_bookings_check_dates CHECK (check_out > check_in),
    CONSTRAINT chk_hotel_bookings_guests CHECK (guests > 0)
);
CREATE INDEX IF NOT EXISTS idx_hotel_bookings_user_id ON hotel_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_hotel_bookings_hotel_id ON hotel_bookings(hotel_id);
CREATE INDEX IF NOT EXISTS idx_hotel_bookings_room_id ON hotel_bookings(room_id);
CREATE INDEX IF NOT EXISTS idx_hotel_bookings_status ON hotel_bookings(status);
CREATE INDEX IF NOT EXISTS idx_hotel_bookings_check_in ON hotel_bookings(check_in);
-- ============================================
-- Hotel Reviews Table
-- ============================================
CREATE TABLE IF NOT EXISTS hotel_reviews (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    comment VARCHAR(2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hotel_reviews_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT fk_hotel_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_hotel_reviews_rating CHECK (
        rating >= 1
        AND rating <= 5
    )
);
CREATE INDEX IF NOT EXISTS idx_hotel_reviews_hotel_id ON hotel_reviews(hotel_id);
CREATE INDEX IF NOT EXISTS idx_hotel_reviews_user_id ON hotel_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_hotel_reviews_rating ON hotel_reviews(rating);
-- ============================================
-- Cinemas Table
-- ============================================
CREATE TABLE IF NOT EXISTS cinemas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    facilities VARCHAR(1000),
    phone_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_cinemas_city ON cinemas(city);
-- ============================================
-- Screens Table
-- ============================================
CREATE TABLE IF NOT EXISTS screens (
    id BIGSERIAL PRIMARY KEY,
    cinema_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    capacity INTEGER,
    screen_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_screens_cinema FOREIGN KEY (cinema_id) REFERENCES cinemas(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_screens_cinema_id ON screens(cinema_id);
CREATE INDEX IF NOT EXISTS idx_screens_screen_type ON screens(screen_type);
-- ============================================
-- Seats Table
-- ============================================
CREATE TABLE IF NOT EXISTS seats (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT NOT NULL,
    row VARCHAR(10) NOT NULL,
    number INTEGER NOT NULL,
    seat_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seats_screen FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
    CONSTRAINT uk_seats_screen_row_number UNIQUE (screen_id, row, number)
);
CREATE INDEX IF NOT EXISTS idx_seats_screen_id ON seats(screen_id);
CREATE INDEX IF NOT EXISTS idx_seats_row_number ON seats(row, number);
-- ============================================
-- Movies Table
-- ============================================
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    genre VARCHAR(100),
    duration INTEGER,
    rating DECIMAL(3, 1),
    poster_url VARCHAR(500),
    trailer_url VARCHAR(500),
    release_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_movies_rating CHECK (
        rating >= 0
        AND rating <= 10
    ),
    CONSTRAINT chk_movies_duration CHECK (duration > 0)
);
CREATE INDEX IF NOT EXISTS idx_movies_title ON movies(title);
CREATE INDEX IF NOT EXISTS idx_movies_genre ON movies(genre);
CREATE INDEX IF NOT EXISTS idx_movies_release_date ON movies(release_date);
-- ============================================
-- Showtimes Table
-- ============================================
CREATE TABLE IF NOT EXISTS showtimes (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_showtimes_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_showtimes_screen FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE,
    CONSTRAINT chk_showtimes_time CHECK (end_time > start_time),
    CONSTRAINT chk_showtimes_price CHECK (price >= 0)
);
CREATE INDEX IF NOT EXISTS idx_showtimes_movie_id ON showtimes(movie_id);
CREATE INDEX IF NOT EXISTS idx_showtimes_screen_id ON showtimes(screen_id);
CREATE INDEX IF NOT EXISTS idx_showtimes_start_time ON showtimes(start_time);
-- ============================================
-- Movie Bookings Table
-- ============================================
CREATE TABLE IF NOT EXISTS movie_bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movie_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_bookings_showtime FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    CONSTRAINT chk_movie_bookings_price CHECK (total_price >= 0)
);
CREATE INDEX IF NOT EXISTS idx_movie_bookings_user_id ON movie_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_movie_bookings_showtime_id ON movie_bookings(showtime_id);
CREATE INDEX IF NOT EXISTS idx_movie_bookings_status ON movie_bookings(status);
CREATE INDEX IF NOT EXISTS idx_movie_bookings_booking_date ON movie_bookings(booking_date);
-- ============================================
-- Booking Seats Table
-- ============================================
CREATE TABLE IF NOT EXISTS booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_seats_booking FOREIGN KEY (booking_id) REFERENCES movie_bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_seats_seat FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE,
    CONSTRAINT uk_booking_seats_showtime_seat UNIQUE (booking_id, seat_id),
    CONSTRAINT chk_booking_seats_price CHECK (price >= 0)
);
CREATE INDEX IF NOT EXISTS idx_booking_seats_booking_id ON booking_seats(booking_id);
CREATE INDEX IF NOT EXISTS idx_booking_seats_seat_id ON booking_seats(seat_id);
-- ============================================
-- Payments Table
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    booking_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_payments_amount CHECK (amount >= 0),
    CONSTRAINT chk_payments_booking_type CHECK (
        booking_type IN ('MOVIE', 'HOTEL', 'RESTAURANT', 'PARK')
    )
);
CREATE INDEX IF NOT EXISTS idx_payments_booking_id ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_payments_booking_type ON payments(booking_type);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_transaction_id ON payments(transaction_id);
-- ============================================
-- Content Table
-- ============================================
CREATE TABLE IF NOT EXISTS content (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    price VARCHAR(255),
    description TEXT,
    thumbnail VARCHAR(255),
    images TEXT,
    owner_id BIGINT,
    duration INTEGER,
    release_date VARCHAR(255),
    location VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);
CREATE INDEX IF NOT EXISTS idx_content_type ON content(type);
CREATE INDEX IF NOT EXISTS idx_content_status ON content(status);
-- ============================================
-- Content Bookings Table
-- ============================================
CREATE TABLE IF NOT EXISTS content_bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_reference VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    visit_date DATE NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    -- PENDING, CONFIRMED, CANCELLED
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_content_booking_content FOREIGN KEY (content_id) REFERENCES content(id)
);
CREATE INDEX IF NOT EXISTS idx_content_booking_reference ON content_bookings(booking_reference);
CREATE INDEX IF NOT EXISTS idx_content_booking_user ON content_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_content_booking_content ON content_bookings(content_id);
-- ============================================
-- Triggers for updated_at columns
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';
-- Drop existing triggers if they exist and recreate them
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE
UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_hotels_updated_at ON hotels;
CREATE TRIGGER update_hotels_updated_at BEFORE
UPDATE ON hotels FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_rooms_updated_at ON rooms;
CREATE TRIGGER update_rooms_updated_at BEFORE
UPDATE ON rooms FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_hotel_bookings_updated_at ON hotel_bookings;
CREATE TRIGGER update_hotel_bookings_updated_at BEFORE
UPDATE ON hotel_bookings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_cinemas_updated_at ON cinemas;
CREATE TRIGGER update_cinemas_updated_at BEFORE
UPDATE ON cinemas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_screens_updated_at ON screens;
CREATE TRIGGER update_screens_updated_at BEFORE
UPDATE ON screens FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_seats_updated_at ON seats;
CREATE TRIGGER update_seats_updated_at BEFORE
UPDATE ON seats FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_movies_updated_at ON movies;
CREATE TRIGGER update_movies_updated_at BEFORE
UPDATE ON movies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_showtimes_updated_at ON showtimes;
CREATE TRIGGER update_showtimes_updated_at BEFORE
UPDATE ON showtimes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_movie_bookings_updated_at ON movie_bookings;
CREATE TRIGGER update_movie_bookings_updated_at BEFORE
UPDATE ON movie_bookings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_payments_updated_at ON payments;
CREATE TRIGGER update_payments_updated_at BEFORE
UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_content_updated_at ON content;
CREATE TRIGGER update_content_updated_at BEFORE
UPDATE ON content FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS update_content_bookings_updated_at ON content_bookings;
CREATE TRIGGER update_content_bookings_updated_at BEFORE
UPDATE ON content_bookings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
-- ============================================
-- Refresh Token Table (Added for Feature)
-- ============================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
-- ============================================
-- End of Schema Initialization
-- ============================================