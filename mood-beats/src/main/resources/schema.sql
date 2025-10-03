-- MoodBeats Database Schema
-- This script sets up the initial database structure

-- Drop existing tables if they exist (for development)
DROP TABLE IF EXISTS recommendations CASCADE;
DROP TABLE IF EXISTS mood_analyses CASCADE;
DROP TABLE IF EXISTS playlist_songs CASCADE;
DROP TABLE IF EXISTS playlists CASCADE;
DROP TABLE IF EXISTS songs CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Genres table
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    characteristics TEXT,
    energy_min INTEGER DEFAULT 1,
    energy_max INTEGER DEFAULT 10,
    valence_min INTEGER DEFAULT 1,
    valence_max INTEGER DEFAULT 10,
    arousal_min INTEGER DEFAULT 1,
    arousal_max INTEGER DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Songs table (for now we'll have sample data, later can integrate with Spotify)
CREATE TABLE songs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    genre_id BIGINT REFERENCES genres(id),
    energy_level INTEGER DEFAULT 5,
    valence INTEGER DEFAULT 5,
    arousal INTEGER DEFAULT 5,
    tempo INTEGER,
    duration_ms BIGINT,
    spotify_id VARCHAR(50),
    preview_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Mood analyses table - stores AI analysis results
CREATE TABLE mood_analyses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    mood_description TEXT NOT NULL,
    primary_emotions JSONB, -- ["happiness", "nostalgia"]
    energy_level INTEGER, -- 1-10
    arousal_level INTEGER, -- 1-10
    valence INTEGER, -- 1-10
    ai_analysis_raw JSONB, -- raw AI response
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Playlists table
CREATE TABLE playlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    mood_analysis_id BIGINT REFERENCES mood_analyses(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Playlist songs junction table
CREATE TABLE playlist_songs (
    id BIGSERIAL PRIMARY KEY,
    playlist_id BIGINT REFERENCES playlists(id) ON DELETE CASCADE,
    song_id BIGINT REFERENCES songs(id),
    position INTEGER NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(playlist_id, position)
);

-- Recommendations table - tracks what was recommended and user feedback
CREATE TABLE recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    mood_analysis_id BIGINT REFERENCES mood_analyses(id),
    song_id BIGINT REFERENCES songs(id),
    recommendation_score DECIMAL(3,2), -- 0.00 to 1.00
    user_rating INTEGER, -- 1-5 stars, nullable until user rates
    user_feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_mood_analyses_user_id ON mood_analyses(user_id);
CREATE INDEX idx_mood_analyses_created_at ON mood_analyses(created_at);
CREATE INDEX idx_songs_genre_id ON songs(genre_id);
CREATE INDEX idx_songs_energy_valence ON songs(energy_level, valence);
CREATE INDEX idx_playlists_user_id ON playlists(user_id);
CREATE INDEX idx_recommendations_user_id ON recommendations(user_id);
CREATE INDEX idx_recommendations_mood_analysis_id ON recommendations(mood_analysis_id);
