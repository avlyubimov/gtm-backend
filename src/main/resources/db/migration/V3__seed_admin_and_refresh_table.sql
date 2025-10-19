CREATE TABLE IF NOT EXISTS gtm_refresh_token (
                                                 id UUID PRIMARY KEY,
                                                 user_id BIGINT NOT NULL REFERENCES gtm_user(id) ON DELETE CASCADE,
                                                 jti TEXT NOT NULL UNIQUE,
                                                 expires_at TIMESTAMPTZ NOT NULL,
                                                 revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_refresh_user ON gtm_refresh_token(user_id);

DO $$
    DECLARE
        v_id BIGINT;
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM gtm_user WHERE username = 'admin') THEN
            INSERT INTO gtm_user(full_name, phone, email, username, password_hash, status, created_at, updated_at)
            VALUES (
                       'Administrator',
                       '+70000000000',
                       'admin@example.com',
                       'admin',
                       '$2a$12$kwmnUiikYhZJl040VGRbEuTpdyTuGTBfokBmTR.kfIvZa2AH4qKti',
                       'ACTIVE',
                       now(), now()
                   )
            RETURNING id INTO v_id;

            INSERT INTO gtm_user_roles(user_id, role) VALUES (v_id, 'ADMIN');
        END IF;
    END $$;
