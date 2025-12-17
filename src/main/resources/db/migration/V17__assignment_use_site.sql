-- Migrate assignments to reference Site instead of Facility

-- 1) Add site_id column
alter table gtm_assignment
    add column if not exists site_id bigint null references gtm_site(id);

-- 2) Backfill site_id from existing facility assignments
update gtm_assignment a
set site_id = f.site_id
from gtm_facility f
where a.site_id is null
  and a.facility_id = f.id;

-- 3) Ensure only one active assignment per site (partial unique index)
drop index if exists uq_assignment_site_active;
create unique index uq_assignment_site_active
    on gtm_assignment(site_id)
    where active = true and is_deleted = false;

-- 4) Drop old unique constraint by facility (if exists)
do $$
begin
    if exists (
        select 1 from information_schema.table_constraints 
        where table_name = 'gtm_assignment' and constraint_name = 'uq_assignment_unique'
    ) then
        alter table gtm_assignment drop constraint uq_assignment_unique;
    end if;
end $$;

-- 5) Make site_id not null
alter table gtm_assignment
    alter column site_id set not null;

-- 6) Drop obsolete facility_id column
do $$
begin
    if exists (
        select 1 from information_schema.columns 
        where table_name = 'gtm_assignment' and column_name = 'facility_id'
    ) then
        alter table gtm_assignment drop column facility_id;
    end if;
end $$;
