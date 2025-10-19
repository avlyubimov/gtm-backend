create table if not exists gtm_assignment (
                                              id           bigserial primary key,
                                              facility_id  bigint       not null references gtm_facility(id),
                                              user_id      bigint       not null references gtm_user(id),
                                              active       boolean      not null default true,

                                              created_at   timestamptz  not null default now(),
                                              updated_at   timestamptz  not null default now(),
                                              is_deleted   boolean       not null default false,
                                              deleted_at   timestamptz,

                                              constraint uq_assignment_unique
                                                  unique (facility_id, user_id, active)
);

create index if not exists ix_assignment_user_active
    on gtm_assignment(user_id) where active = true and is_deleted = false;

create index if not exists ix_assignment_facility_active
    on gtm_assignment(facility_id) where active = true and is_deleted = false;
