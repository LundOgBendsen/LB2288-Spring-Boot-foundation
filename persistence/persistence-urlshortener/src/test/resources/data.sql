insert into users(username, password) values('user1', 'password1');
insert into users(username, password) values('user2', 'password2');
insert into users(username, password) values('user3', 'password3');

insert into token(token, protect_token, target_url, user_username) values('token1', null, 'https://dr.dk', 'user1');

insert into token(token, protect_token, target_url, user_username) values('token2', 'pt2', 'https://dr.dk', 'user2');
insert into token(token, protect_token, target_url, user_username) values('token3', 'pt2', 'https://dr.dk', 'user2');
insert into token(token, protect_token, target_url, user_username) values('token4', 'pt2', 'https://dr.dk', 'user2');

insert into token(token, protect_token, target_url, user_username) values('token5', 'pt3', 'https://dr.dk', 'user3');
insert into token(token, protect_token, target_url, user_username) values('token6', null, 'https://dr.dk', 'user3');
insert into token(token, protect_token, target_url, user_username) values('token7', null, 'https://dr.dk', 'user3');
