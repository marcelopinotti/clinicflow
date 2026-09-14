-- Issue #22: garantir unicidade do slot do médico no banco (corrida / TOCTOU).
--
-- A validação de conflito em código (existeConflitoNoIntervalo) é read-then-write:
-- sob concorrência, duas requisições para o mesmo médico/slot podem ambas passar
-- pela checagem e gravar, causando double-booking. A garantia definitiva precisa
-- ser uma constraint no banco.
--
-- Estratégia: índice único PARCIAL sobre (medico_id, scheduled_at) restrito às
-- consultas ATIVAS. Consultas CANCELADA ficam de fora do índice, de modo que
-- cancelar uma consulta libera o slot para ser reusado (uma cancelada não bloqueia
-- nova marcação no mesmo horário). Isso é coerente com a regra de negócio usada na
-- checagem em código, que também ignora o status CANCELADA.
--
-- Índice parcial é um recurso do PostgreSQL (dialeto-alvo do projeto); por isso a
-- regra vive aqui no Flyway e não como @Table(uniqueConstraints) na entidade JPA,
-- que só expressa unicidade total (incluindo canceladas).

CREATE UNIQUE INDEX IF NOT EXISTS uq_consultas_medico_slot_ativa
    ON consultas (medico_id, scheduled_at)
    WHERE status <> 'CANCELADA';
