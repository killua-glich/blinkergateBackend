CREATE TYPE quest_category AS ENUM ('SKINCARE', 'EDUCATION', 'PRODUCTIVITY', 'HEALTH');
CREATE TYPE repeat_type AS ENUM ('DAILY', 'SINGLE');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE quests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category quest_category NOT NULL,
    repeat_type repeat_type NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    last_completed DATE,
    created_at TIMESTAMP DEFAULT NOW()
);
