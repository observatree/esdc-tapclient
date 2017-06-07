-- This script must be executed after UWS and TAP scripts 
-- (i.e the database must be already created.)
-- share_schema must be already created

-- DROP TABLE share_schema.accessible_public_group_tables

insert into share_schema.groups (group_id, title, description, creator) values ('satgaia_1','Public group','Public group','satgaia')

create table share_schema.accessible_public_group_tables(
	user_id varchar NOT NULL,
	table_schema varchar NOT NULL,
	table_name varchar NOT NULL,
	table_owner varchar NOT NULL,
	CONSTRAINT accessible_public_group_tables_pk PRIMARY KEY (user_id, table_schema, table_name, table_owner)
);