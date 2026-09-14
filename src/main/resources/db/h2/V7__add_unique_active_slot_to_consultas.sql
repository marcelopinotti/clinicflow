-- H2 does not support PostgreSQL partial indexes. NULL values in this generated
-- column represent cancelled appointments and remain outside the unique index.
ALTER TABLE consultas ADD COLUMN active_scheduled_at TIMESTAMP
    GENERATED ALWAYS AS (
        CASE WHEN status <> 'CANCELADA' THEN scheduled_at ELSE NULL END
    );

CREATE UNIQUE INDEX IF NOT EXISTS uq_consultas_medico_slot_ativa
    ON consultas (medico_id, active_scheduled_at);
