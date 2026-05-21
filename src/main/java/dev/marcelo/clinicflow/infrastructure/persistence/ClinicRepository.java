package dev.marcelo.clinicflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<ClinicEntity, Long> {
}
