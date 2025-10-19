alter table gtm_contract add column if not exists is_deleted boolean not null default false;
alter table gtm_contract add column if not exists deleted_at timestamptz null;

alter table gtm_site add column if not exists is_deleted boolean not null default false;
alter table gtm_site add column if not exists deleted_at timestamptz null;

alter table gtm_facility add column if not exists is_deleted boolean not null default false;
alter table gtm_facility add column if not exists deleted_at timestamptz null;

alter table gtm_user add column if not exists is_deleted boolean not null default false;
alter table gtm_user add column if not exists deleted_at timestamptz null;

alter table gtm_contract drop constraint if exists gtm_contract_number_key;
alter table gtm_site     drop constraint if exists uk_site_contract_code;
alter table gtm_facility drop constraint if exists uk_facility_site_code;
alter table gtm_user     drop constraint if exists gtm_user_email_key;
alter table gtm_user     drop constraint if exists gtm_user_username_key;
alter table gtm_user     drop constraint if exists gtm_user_phone_key;

create unique index if not exists uq_gtm_contract_number_live
    on gtm_contract (lower(number)) where is_deleted = false;

create unique index if not exists uq_gtm_site_contract_code_live
    on gtm_site (contract_id, lower(code)) where is_deleted = false;

create unique index if not exists uq_gtm_facility_site_code_live
    on gtm_facility (site_id, lower(code)) where is_deleted = false;

create unique index if not exists uq_gtm_user_email_live
    on gtm_user (lower(email)) where is_deleted = false;

create unique index if not exists uq_gtm_user_username_live
    on gtm_user (lower(username)) where is_deleted = false;

create unique index if not exists uq_gtm_user_phone_live
    on gtm_user (phone) where is_deleted = false;

create index if not exists ix_gtm_contract_is_deleted on gtm_contract (is_deleted);
create index if not exists ix_gtm_site_is_deleted     on gtm_site (is_deleted);
create index if not exists ix_gtm_facility_is_deleted on gtm_facility (is_deleted);
create index if not exists ix_gtm_user_is_deleted     on gtm_user (is_deleted);

create index if not exists ix_gtm_contract_deleted_at on gtm_contract (deleted_at);
create index if not exists ix_gtm_site_deleted_at     on gtm_site (deleted_at);
create index if not exists ix_gtm_facility_deleted_at on gtm_facility (deleted_at);
create index if not exists ix_gtm_user_deleted_at     on gtm_user (deleted_at);
