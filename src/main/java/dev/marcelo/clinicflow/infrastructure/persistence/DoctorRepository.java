package dev.marcelo.clinicflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<DoctorEntity,Long> {

    List<DoctorEntity> findByClinics_Id(Long clinicId);
}
