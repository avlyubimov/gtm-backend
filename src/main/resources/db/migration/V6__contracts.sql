create table if not exists gtm_contract (
                                        id           bigserial primary key,
                                        number       text        not null unique,
                                        signed_at    date        not null,
                                        customer     text        not null,
                                        customer_full_name text  not null,
                                        created_at   timestamptz not null default now(),
                                        updated_at   timestamptz not null default now()
);

create or replace function set_updated_at()
    returns trigger as $$
begin
    new.updated_at := now();
    return new;
end; $$ language plpgsql;

drop trigger if exists trg_contract_updated on gtm_contract;
create trigger trg_contract_updated
    before update on gtm_contract
    for each row execute procedure set_updated_at();

create index if not exists idx_contract_signed_at on gtm_contract (signed_at);
create index if not exists idx_contract_customer on gtm_contract using gin (
                                                                        to_tsvector('simple', coalesce(customer,'') || ' ' || coalesce(customer_full_name,''))
    );
