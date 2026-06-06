package dev.marcelo.clinicflow.core.enums;

public enum DoctorSpecialty {
    CARDIOLOGY("Cardiologista"),
    DERMATOLOGY("Dermatologista"),
    PEDIATRICS("Pediatra"),
    ORTHOPEDICS("Orthopedista"),
    NEUROLOGY("Neurologista");

    private final String descricao;

    DoctorSpecialty(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}




