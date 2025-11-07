CREATE TABLE IF NOT EXISTS users (
    userId UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(createdAt);


COMMENT ON TABLE users IS 'Пользователи';
COMMENT ON COLUMN users.userId IS 'Уникальный идентификатор пользователя (UUID)';
COMMENT ON COLUMN users.username IS 'Имя пользователя (уникальное)';
COMMENT ON COLUMN users.email IS 'Электронная почта пользователя (уникальная)';
COMMENT ON COLUMN users.password IS 'Хэш пароля пользователя';
COMMENT ON COLUMN users.createdAt IS 'Дата и время создания записи';
