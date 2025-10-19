create table if not exists gtm_facility (
                                            id           bigserial primary key,
                                            site_id      bigint      not null references gtm_site(id) on delete cascade,
                                            name         text        not null,
                                            code         text        not null,
                                            created_at   timestamptz not null default now(),
                                            updated_at   timestamptz not null default now(),
                                            unique (site_id, code)
);

create or replace function set_updated_at()
    returns trigger as $$
begin
    new.updated_at := now();
    return new;
end;
$$ language plpgsql;

drop trigger if exists trg_gtm_facility_updated on gtm_facility;
create trigger trg_gtm_facility_updated
    before update on gtm_facility
    for each row execute procedure set_updated_at();

create index if not exists idx_gtm_facility_site   on gtm_facility(site_id);
create index if not exists idx_gtm_facility_code   on gtm_facility(code);
