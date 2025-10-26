create sequence hibernate_sequence start with 1 increment by 1;
create table gamer (id bigint not null, alias varchar(255), avatar varchar(255), primary key (id));