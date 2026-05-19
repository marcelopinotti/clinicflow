package dev.marcelo.clinicflow.infrastructure.persistence;

import dev.marcelo.clinicflow.core.enums.ClinicStatus;
import dev.marcelo.clinicflow.core.enums.DoctorSpecialty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "clinicas")
public class ClinicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private ClinicStatus status;
    private Set<DoctorSpecialty> specialties;

    public ClinicEntity() {
    }

    public ClinicEntity(Long id, String name, String address, String phone, String email, ClinicStatus status, Set<DoctorSpecialty> specialties) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.specialties = specialties;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClinicStatus getStatus() {
        return status;
    }

    public void setStatus(ClinicStatus status) {
        this.status = status;
    }

    public Set<DoctorSpecialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<DoctorSpecialty> specialties) {
        this.specialties = specialties;
    }

    @Override
    public String toString() {
        return "ClinicEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", specialties=" + specialties +
                '}';
    }
}


