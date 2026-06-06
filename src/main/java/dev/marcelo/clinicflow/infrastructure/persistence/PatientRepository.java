package dev.marcelo.clinicflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity,Long> {
}
