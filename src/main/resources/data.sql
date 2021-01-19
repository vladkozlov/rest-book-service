INSERT INTO security_role(id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO security_role(id, role_name) VALUES (2, 'ROLE_LIBRARIAN');


-- librarian:librarian
INSERT INTO security_user(id, username, password, first_name, last_name, is_enabled) VALUES (1, 'librarian', '$2a$12$g2T1Pw8duNlJ5lJuYi2nJuDScTW.7QNfrgT4hLFLCMIWxWMOn.YQ.', 'Librarian', 'Admin', TRUE);
-- user:user
INSERT INTO security_user(id, username, password, first_name, last_name, is_enabled) VALUES (2, 'user', '$2a$12$TXdNB/O4FBvCjteuKE4UQuaAExzSC4IcCrjCspmV/dOs0yTfMBzbu', 'User', 'Simple', TRUE);


INSERT INTO user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO user_role(user_id, role_id) VALUES (1, 2);
INSERT INTO user_role(user_id, role_id) VALUES (2, 1);

INSERT INTO book(id, ISBN, title) VALUES (1, '9780545010221', 'Harry Potter and the Deathly Hallows');
INSERT INTO book(id, ISBN, title) VALUES (2, '9780747538486', 'Harry Potter and the Chamber of Secrets');