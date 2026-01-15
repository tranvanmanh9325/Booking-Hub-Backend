-- Add owner_id to content table
ALTER TABLE content
ADD COLUMN owner_id BIGINT;
ALTER TABLE content
ADD CONSTRAINT fk_content_owner FOREIGN KEY (owner_id) REFERENCES users(id);
-- Create content_bookings table
CREATE TABLE content_bookings (
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
CREATE INDEX idx_content_booking_reference ON content_bookings(booking_reference);
CREATE INDEX idx_content_booking_user ON content_bookings(user_id);
CREATE INDEX idx_content_booking_content ON content_bookings(content_id);