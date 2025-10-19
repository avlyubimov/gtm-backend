create table if not exists gtm_cycle (
                                         id            bigserial primary key,
                                         name          varchar(255) not null,
                                         period_start  timestamptz  not null,
                                         period_end    timestamptz  not null,
                                         facility_id   bigint       not null references gtm_facility(id) on delete restrict,
                                         status        varchar(16)  not null,

                                         is_deleted    boolean not null default false,
                                         deleted_at    timestamptz,
                                         created_at    timestamptz not null default now(),
                                         updated_at    timestamptz not null default now(),

                                         constraint chk_cycle_period check (period_end > period_start)
);

create index if not exists idx_cycle_facility on gtm_cycle(facility_id);
create index if not exists idx_cycle_status on gtm_cycle(status);
create index if not exists idx_cycle_not_deleted on gtm_cycle(is_deleted) where is_deleted = false;

create unique index if not exists uq_active_cycle_per_facility
    on gtm_cycle(facility_id)
    where status = 'ACTIVE';
