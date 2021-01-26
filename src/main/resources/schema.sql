CREATE TABLE roles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name varchar(100) DEFAULT NULL
);

CREATE TABLE users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(100) NOT NULL,
    first_name varchar(250) NOT NULL,
    last_name varchar(250) NOT NULL,
    is_enabled boolean NOT NULL
);

CREATE TABLE user_role(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT FK_users_ID FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_roles_ID FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE book(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ISBN varchar(13) NOT NULL,
    title varchar(100) NOT NULL
);

CREATE TABLE BOOK_BORROW(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    expire_at DATE NOT NULL
);