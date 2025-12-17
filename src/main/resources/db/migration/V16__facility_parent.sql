-- Add parent_id to gtm_facility for tree structure
alter table gtm_facility
    add column if not exists parent_id bigint null references gtm_facility(id) on delete restrict;

create index if not exists idx_facility_parent on gtm_facility(parent_id);
create index if not exists idx_facility_site_parent on gtm_facility(site_id, parent_id);
