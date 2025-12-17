create table if not exists gtm_notification (
    id           bigserial primary key,
    user_id      bigint       not null references gtm_user(id) on delete cascade,
    type         varchar(64)  not null,
    title        text         not null,
    message      text         not null,
    payload      jsonb,
    is_read      boolean      not null default false,

    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now(),
    is_deleted   boolean      not null default false,
    deleted_at   timestamptz
);

create index if not exists ix_notification_user_created on gtm_notification(user_id, created_at desc) where is_deleted = false;
create index if not exists ix_notification_user_read on gtm_notification(user_id, is_read) where is_deleted = false;
create index if not exists ix_notification_type on gtm_notification(type) where is_deleted = false;

-- trigger to update updated_at
drop trigger if exists trg_notification_updated on gtm_notification;
create trigger trg_notification_updated
    before update on gtm_notification
    for each row execute procedure set_updated_at();
