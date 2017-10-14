﻿drop table if exists audit_record;
drop table if exists audit;

create extension if not exists hstore;

create table audit (
  id uuid NOT NULL,
  act character varying(32) not null,
  app character varying(64) not null,
  client character varying(32),
  server character varying(32),
  user_id integer,
  company_id integer,
  tags hstore not null,
  started timestamp without time zone not null,
  completed timestamp without time zone not null,
  primary key (id)
) with (oids = false);

create index audit_act on audit using btree (act asc nulls last);
create index audit_app on audit using btree (app asc nulls last);
create index audit_client on audit using btree (client asc nulls last);
create index audit_server on audit using btree (server asc nulls last);
create index audit_user_id on audit using btree (user_id asc nulls last);
create index audit_company_id on audit using btree (company_id asc nulls last);
create index audit_tags on audit using gin (tags);

create table audit_record (
  id uuid not null,
  audit_id uuid not null,
  t character varying(128) not null,
  pk character varying(128)[] not null,
  changes jsonb not null,
  primary key (id)
) with (oids = false);

alter table audit_record add constraint audit_record_audit_id foreign key (audit_id) references audit (id) match simple;
create index audit_record_t on audit_record using btree (t asc nulls last);
create index audit_record_pk on audit_record using btree (pk asc nulls last);
create index audit_record_changes on audit_record using gin (changes);