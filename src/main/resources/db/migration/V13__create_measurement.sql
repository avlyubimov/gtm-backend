create table if not exists gtm_measurement (
                                               id           bigserial primary key,
                                               facility_id  bigint not null references gtm_facility(id),
                                               point_id     bigint not null references gtm_point(id),
                                               cycle_id     bigint not null references gtm_cycle(id),

                                               type         varchar(40) not null,
                                               payload      jsonb        not null,
                                               measured_at  timestamptz  not null default now(),

                                               created_at   timestamptz  not null default now(),
                                               updated_at   timestamptz  not null default now(),
                                               is_deleted   boolean      not null default false,
                                               deleted_at   timestamptz
);

create index if not exists idx_gtm_measurement_point on gtm_measurement(point_id) where is_deleted = false;
create index if not exists idx_gtm_measurement_cycle on gtm_measurement(cycle_id) where is_deleted = false;
create index if not exists idx_gtm_measurement_facility on gtm_measurement(facility_id) where is_deleted = false;
create index if not exists idx_gtm_measurement_type on gtm_measurement(type) where is_deleted = false;
create index if not exists idx_gtm_measurement_payload_gin on gtm_measurement using gin (payload);

create index if not exists idx_gtm_measurement_point_type_time
    on gtm_measurement(point_id, type, measured_at desc) where is_deleted = false;
