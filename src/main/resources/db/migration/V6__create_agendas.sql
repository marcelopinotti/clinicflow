-- Janelas de atendimento (agenda/disponibilidade) por médico:
-- cada linha representa uma janela em um dia da semana com duração de slot.

CREATE TABLE IF NOT EXISTS agendas (
    id BIGSERIAL PRIMARY KEY,
    medico_id BIGINT NOT NULL REFERENCES medicos(id),
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_minutes INTEGER NOT NULL
);
