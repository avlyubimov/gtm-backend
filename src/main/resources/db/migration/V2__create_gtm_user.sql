CREATE TABLE gtm_user (
                          id            BIGSERIAL PRIMARY KEY,
                          full_name     TEXT        NOT NULL,
                          phone         TEXT        NOT NULL,
                          email         TEXT        NOT NULL,
                          username      TEXT        NOT NULL,
                          password_hash TEXT        NOT NULL,
                          status        TEXT        NOT NULL DEFAULT 'ACTIVE',
                          created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                          last_login_at TIMESTAMPTZ,
                          date_of_birth DATE,
                          CONSTRAINT chk_phone_e164 CHECK (phone ~ '^\+\d{7,15}$')
);

CREATE TABLE gtm_user_roles (
                                user_id BIGINT NOT NULL REFERENCES gtm_user(id) ON DELETE CASCADE,
                                role    TEXT   NOT NULL,
                                PRIMARY KEY (user_id, role)
);

CREATE UNIQUE INDEX ux_gtm_user_email_ci     ON gtm_user (lower(email));
CREATE UNIQUE INDEX ux_gtm_user_username_ci  ON gtm_user (lower(username));
CREATE UNIQUE INDEX ux_gtm_user_phone_e164   ON gtm_user (phone);

CREATE OR REPLACE FUNCTION trg_set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at
    BEFORE UPDATE ON gtm_user
    FOR EACH ROW EXECUTE FUNCTION trg_set_updated_at();