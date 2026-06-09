package dev.marcelo.clinicflow.core.exceptions;

public class ClinicAlreadyExistsException extends RuntimeException {

    public ClinicAlreadyExistsException(String cnpj) {
        super("Já existe uma clínica cadastrada com o CNPJ: " + cnpj);
    }
}
