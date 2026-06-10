package dev.marcelo.clinicflow.core.enums;

import java.util.Set;

public enum AppointmentStatus {
    AGENDADA,
    CONFIRMADA,
    CANCELADA,
    REALIZADA,
    NO_SHOW;

    private Set<AppointmentStatus> allowedTransitions;

    static {
        AGENDADA.allowedTransitions = Set.of(CONFIRMADA, CANCELADA);
        CONFIRMADA.allowedTransitions = Set.of(REALIZADA, NO_SHOW, CANCELADA);
        CANCELADA.allowedTransitions = Set.of();
        REALIZADA.allowedTransitions = Set.of();
        NO_SHOW.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(AppointmentStatus target) {
        return allowedTransitions.contains(target);
    }
}
