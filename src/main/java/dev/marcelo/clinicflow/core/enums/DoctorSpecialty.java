package dev.marcelo.clinicflow.core.enums;

public enum DoctorSpecialty {
    CARDIOLOGY("cardiologista"),
    DERMATOLOGY("dermatologista"),
    PEDIATRICS("pediatra"),
    ORTHOPEDICS("orthopedista"),
    NEUROLOGY("neurologista");

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




