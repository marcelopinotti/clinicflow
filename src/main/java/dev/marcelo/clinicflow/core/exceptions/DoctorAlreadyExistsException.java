package dev.marcelo.clinicflow.core.exceptions;

public class DoctorAlreadyExistsException extends RuntimeException {

    private DoctorAlreadyExistsException(String message) {
        super(message);
    }

    public static DoctorAlreadyExistsException porCpf(String cpf) {
        return new DoctorAlreadyExistsException("Já existe um médico cadastrado com o CPF: " + cpf);
    }

    public static DoctorAlreadyExistsException porCrm(String crm) {
        return new DoctorAlreadyExistsException("Já existe um médico cadastrado com o CRM: " + crm);
    }
}
