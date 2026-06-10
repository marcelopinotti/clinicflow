package dev.marcelo.clinicflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorEntity,Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCrm(String crm);
}
