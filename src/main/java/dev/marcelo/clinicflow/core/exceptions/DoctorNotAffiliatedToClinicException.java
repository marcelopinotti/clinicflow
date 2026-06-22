package dev.marcelo.clinicflow.core.exceptions;

public class DoctorNotAffiliatedToClinicException extends RuntimeException {

    public DoctorNotAffiliatedToClinicException(Long doctorId, Long clinicId) {
        super("O médico " + doctorId + " não atende na clínica " + clinicId);
    }
}
