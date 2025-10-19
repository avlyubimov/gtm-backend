-- 1) Добавляем колонку user_id (если её ещё нет)
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'gtm_measurement' AND column_name = 'user_id'
        ) THEN
            ALTER TABLE gtm_measurement ADD COLUMN user_id bigint;
        END IF;
    END $$;

-- 2) Бэкофилл: если есть колонка created_by, используем её для заполнения user_id
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'gtm_measurement' AND column_name = 'created_by'
        ) THEN
            UPDATE gtm_measurement m
            SET user_id = m.created_by
            WHERE m.user_id IS NULL;
        END IF;
    END $$;

-- 3) Делаем user_id NOT NULL (после бэкофилла не должно остаться NULL)
ALTER TABLE gtm_measurement
    ALTER COLUMN user_id SET NOT NULL;

-- 4) Внешний ключ на пользователя
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'fk_measurement_user'
        ) THEN
            ALTER TABLE gtm_measurement
                ADD CONSTRAINT fk_measurement_user
                    FOREIGN KEY (user_id) REFERENCES gtm_user(id);
        END IF;
    END $$;

-- 5) Индексы для быстрого доступа
CREATE INDEX IF NOT EXISTS idx_measurement_user   ON gtm_measurement(user_id);
CREATE INDEX IF NOT EXISTS idx_measurement_cycle  ON gtm_measurement(cycle_id);
CREATE INDEX IF NOT EXISTS idx_measurement_point  ON gtm_measurement(point_id);

-- 6) Частичный уникальный индекс, чтобы запретить дубликаты активных (неудалённых) замеров
--    (по пользователю + циклу + точке). Учитывает soft-delete через is_deleted.
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_indexes WHERE indexname = 'uq_meas_user_cycle_point_active'
        ) THEN
            CREATE UNIQUE INDEX uq_meas_user_cycle_point_active
                ON gtm_measurement(user_id, cycle_id, point_id)
                WHERE is_deleted = false;
        END IF;
    END $$;
