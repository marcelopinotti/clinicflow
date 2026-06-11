-- Especialidades da clínica deixam de ser uma coluna escalar e passam a ser
-- uma coleção normalizada (@ElementCollection), uma linha por especialidade.

CREATE TABLE IF NOT EXISTS clinica_especialidades (
    clinica_id    BIGINT       NOT NULL REFERENCES clinicas(id),
    especialidade VARCHAR(50)  NOT NULL,
    PRIMARY KEY (clinica_id, especialidade)
);

ALTER TABLE clinicas DROP COLUMN specialties;