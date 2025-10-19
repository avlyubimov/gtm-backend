create table if not exists gtm_point (
                                         id              bigserial primary key,
                                         name            varchar(255) not null,
                                         type            varchar(32)  not null,
                                         facility_id     bigint not null references gtm_facility(id) on delete restrict,

                                         is_deleted      boolean not null default false,
                                         deleted_at      timestamptz,
                                         created_at      timestamptz not null default now(),
                                         updated_at      timestamptz not null default now()
);

create index if not exists idx_point_facility on gtm_point(facility_id);
create index if not exists idx_point_not_deleted on gtm_point(is_deleted) where is_deleted = false;

alter table gtm_point
    add constraint uq_point_facility_type_name unique (facility_id, type, name);