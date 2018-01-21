create table demo_entity (
	id bigint not null,
	name varchar(255),
	primary key (id)
);

insert into demo_entity (id, name) values (1, 'Demo Entity');