CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

COMMENT ON TABLE users IS 'Пользователи';
COMMENT ON COLUMN users.user_id IS 'Уникальный идентификатор пользователя (UUID)';
COMMENT ON COLUMN users.username IS 'Имя пользователя (уникальное)';
COMMENT ON COLUMN users.email IS 'Электронная почта пользователя (уникальная)';
COMMENT ON COLUMN users.password IS 'Хэш пароля пользователя';
COMMENT ON COLUMN users.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN users.updated_at IS 'Дата и время изменения пользователя';

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username_unique ON users(username);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique ON users(email);

CREATE TABLE IF NOT EXISTS roles (
	role_id uuid DEFAULT gen_random_uuid() NOT NULL,
	role_name varchar(50) NOT NULL,
	CONSTRAINT roles_pk PRIMARY KEY (role_id),
	CONSTRAINT roles_unique UNIQUE (role_name)
);

CREATE TABLE IF NOT EXISTS user_roles (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	user_id uuid NOT NULL,
	role_id uuid NOT NULL,
	CONSTRAINT user_roles_pk PRIMARY KEY (id),
	CONSTRAINT user_roles_unique UNIQUE (user_id, role_id),
	CONSTRAINT user_roles_users_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT user_roles_roles_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
);


 insert into roles
 select gen_random_uuid(), 'ROLE_USER'
 where not exists (select 1
    from roles r
   where r.role_name in ('ROLE_USER'));

  insert into roles
 select gen_random_uuid(), 'ROLE_ADMIN'
 where not exists (select 1
    from roles r
   where r.role_name in ('ROLE_ADMIN'));