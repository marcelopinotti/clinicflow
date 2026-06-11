-- Relacionamento N:N entre médicos e clínicas:
-- um médico pode atuar em várias clínicas e uma clínica tem vários médicos.

CREATE TABLE IF NOT EXISTS medico_clinica (
    medico_id  BIGINT NOT NULL REFERENCES medicos(id),
    clinica_id BIGINT NOT NULL REFERENCES clinicas(id),
    PRIMARY KEY (medico_id, clinica_id)
);
