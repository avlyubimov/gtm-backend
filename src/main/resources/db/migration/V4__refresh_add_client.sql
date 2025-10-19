ALTER TABLE gtm_refresh_token
    ADD COLUMN IF NOT EXISTS ip inet,
    ADD COLUMN IF NOT EXISTS user_agent text,
    ADD COLUMN IF NOT EXISTS device text;

CREATE INDEX IF NOT EXISTS idx_refresh_user_active
    ON gtm_refresh_token(user_id, revoked, expires_at);