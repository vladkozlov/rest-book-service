CREATE TABLE security_role(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name varchar(100) DEFAULT NULL
);

CREATE TABLE security_user(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(100) NOT NULL,
    first_name varchar(250) NOT NULL,
    last_name varchar(250) NOT NULL
);

CREATE TABLE user_role(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT FK_SECURITY_USER_ID FOREIGN KEY (user_id) REFERENCES security_user(id),
    CONSTRAINT FK_SECURITY_ROLE_ID FOREIGN KEY (role_id) REFERENCES security_role(id)
);
