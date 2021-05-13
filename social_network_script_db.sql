create database socialnetworkproject;

use socialnetworkproject;

CREATE TABLE users(
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	phone VARCHAR(16),
	password VARCHAR(255),
	link_avatar VARCHAR(255),
	link_banner VARCHAR(255),
	address VARCHAR(255),
	school VARCHAR(255),
	access_token VARCHAR(1000),
	is_online BOOL DEFAULT false
);
CREATE TABLE post(
	id INT PRIMARY KEY AUTO_INCREMENT,
	poster_id INT,
    content VARCHAR(1000),
    link_image_1 VARCHAR(255),
    link_image_2 VARCHAR(255),
    link_image_3 VARCHAR(255),
    link_image_4 VARCHAR(255),
    link_video VARCHAR(255),
    create_time DATETIME
);

CREATE TABLE comments(
	id INT PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
    sender_id INT,
    content VARCHAR(1000),
    create_time DATETIME
);

CREATE TABLE likes(
	id INT PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
    sender_id INT
);

CREATE TABLE hiddens(
	id INT PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
    hidden_user_id INT
);

CREATE TABLE friend(
	id INT PRIMARY KEY AUTO_INCREMENT,
	user_A_id INT,
    user_B_id INT,
    is_block BOOL DEFAULT false
);
CREATE TABLE friend_request(
	id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT,         /*người được yêu cầu - tôi */
    sender_id INT /* người gửi yêu cầu*/
);

CREATE TABLE notification(
	id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT,
    sender_id INT,
    data_id INT,
    type_notify INT, /* 1: đăng bài viết mới , 2: nhắn tin cho bạn  , 3: gửi lời mời kb cho bạn */
	is_seen BOOL DEFAULT false,
	create_time DATETIME
);
CREATE TABLE participate(
	id INT PRIMARY KEY AUTO_INCREMENT,
	conversation_id INT,
    user_id INT,
    is_seen BOOL DEFAULT false
);
CREATE TABLE conversation(
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
    creator_id INT,
	create_time DATETIME
);
CREATE TABLE message(
	id INT PRIMARY KEY AUTO_INCREMENT,
    conversation_id INT,
	sender_id INT,
    content VARCHAR(255),
    create_time DATETIME
);

CREATE TABLE device(
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    device_token VARCHAR(255),
	create_time DATETIME
);

ALTER TABLE post ADD FOREIGN KEY (poster_id) REFERENCES users(id);

ALTER TABLE comments ADD FOREIGN KEY (post_id) REFERENCES post(id);
ALTER TABLE comments ADD FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE likes ADD FOREIGN KEY (post_id) REFERENCES post(id);
ALTER TABLE likes ADD FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE hiddens ADD FOREIGN KEY (post_id) REFERENCES post(id);
ALTER TABLE hiddens ADD FOREIGN KEY (hidden_user_id) REFERENCES users(id);

ALTER TABLE friend ADD FOREIGN KEY (user_A_id) REFERENCES users(id);
ALTER TABLE friend ADD FOREIGN KEY (user_B_id) REFERENCES users(id);

ALTER TABLE friend_request ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE friend_request ADD FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE notification ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE notification ADD FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE participate ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE participate ADD FOREIGN KEY (conversation_id) REFERENCES conversation(id);

ALTER TABLE conversation ADD FOREIGN KEY (creator_id) REFERENCES users(id);

ALTER TABLE message ADD FOREIGN KEY (conversation_id) REFERENCES conversation(id);
ALTER TABLE message ADD FOREIGN KEY (sender_id) REFERENCES users(id);

ALTER TABLE device ADD FOREIGN KEY (user_id) REFERENCES users(id);
