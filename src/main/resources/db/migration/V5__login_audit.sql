CREATE TABLE IF NOT EXISTS gtm_auth_audit (
                                              id           bigserial PRIMARY KEY,
                                              user_id      bigint NULL REFERENCES gtm_user(id) ON DELETE SET NULL,
                                              login        text,
                                              ip           inet,
                                              user_agent   text,
                                              success      boolean NOT NULL,
                                              error        text,
                                              created_at   timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_auth_audit_created ON gtm_auth_audit(created_at DESC);
