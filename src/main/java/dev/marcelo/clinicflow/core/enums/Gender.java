package dev.marcelo.clinicflow.core.enums;

public enum Gender {
    MALE("Masculino"),
    FEMALE("Femenino"),
    OTHER("Outro");

    private final String descricao;

    Gender(String descricao) {
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
