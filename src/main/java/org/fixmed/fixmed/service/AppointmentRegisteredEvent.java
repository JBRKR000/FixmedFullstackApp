package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Appointments;

public class AppointmentRegisteredEvent {
    private final Appointments appointment;

    public AppointmentRegisteredEvent(Appointments appointment) {
        this.appointment = appointment;
    }

    public Appointments getAppointment() {
        return appointment;
    }
}
