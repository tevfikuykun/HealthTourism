CREATE DATABASE IF NOT EXISTS chat_db;
USE chat_db;

CREATE TABLE IF NOT EXISTS chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participant1_id BIGINT NOT NULL,
    participant2_id BIGINT NOT NULL,
    participant1_type VARCHAR(50) NOT NULL,
    participant2_type VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    last_message_at DATETIME
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    sender_type VARCHAR(50) NOT NULL,
    receiver_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL,
    sent_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    file_url VARCHAR(500)
);

CREATE INDEX idx_sender_receiver ON messages(sender_id, receiver_id);
CREATE INDEX idx_receiver_unread ON messages(receiver_id, is_read);
CREATE INDEX idx_chat_room_participants ON chat_rooms(participant1_id, participant2_id);

