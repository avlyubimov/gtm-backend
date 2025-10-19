create table if not exists gtm_site (
                                        id           bigserial primary key,
                                        contract_id  bigint      not null references gtm_contract(id) on delete cascade,
                                        name         text        not null,
                                        code         text        not null,
                                        created_at   timestamptz not null default now(),
                                        updated_at   timestamptz not null default now(),
                                        unique (contract_id, code)
);

create or replace function set_updated_at()
    returns trigger as $$
begin
    new.updated_at := now();
    return new;
end;
$$ language plpgsql;

drop trigger if exists trg_gtm_site_updated on gtm_site;
create trigger trg_gtm_site_updated
    before update on gtm_site
    for each row execute procedure set_updated_at();

create index if not exists idx_gtm_site_contract on gtm_site(contract_id);
create index if not exists idx_gtm_site_code on gtm_site (code);
