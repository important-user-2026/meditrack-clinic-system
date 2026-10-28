package com.meditrack.service;

import com.meditrack.exception.ResourceNotFoundException;
import com.meditrack.model.Appointment;
import com.meditrack.model.AppointmentStatus;
import com.meditrack.model.Patient;
import com.meditrack.repository.AppointmentRepository;
import com.meditrack.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient("Alice", "alice@clinic.com", "07123", "NHS123", 34);
        patient.setId(1L);
        appointment = new Appointment(1L, "Dr Smith", "general",
                LocalDateTime.of(2026, 11, 1, 10, 0));
    }

    @Test
    void bookAppointmentSucceeds() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment saved = appointmentService.bookAppointment(appointment);
        assertEquals(AppointmentStatus.REQUESTED, saved.getStatus());
    }

    @Test
    void bookAppointmentFailsForUnknownPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.bookAppointment(appointment));
    }

    @Test
    void confirmAppointmentSetsStatus() {
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.confirmAppointment(5L);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void cancelAppointmentSetsStatus() {
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(5L);
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
    }

    @Test
    void confirmAppointmentFailsWhenMissing() {
        when(appointmentRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.confirmAppointment(404L));
    }

    @Test
    void getAppointmentsForPatientReturnsList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(appointment));
        assertEquals(1, appointmentService.getAppointmentsForPatient(1L).size());
    }

    @Test
    void reminderLeadTimeDefaultsWhenNull() {
        assertEquals(24, appointmentService.reminderLeadTimeHours(null));
    }

    @Test
    void reminderLeadTimeForSurgeryEarly() {
        Appointment a = new Appointment(1L, "Dr S", "surgery",
                LocalDateTime.of(2026, 11, 1, 7, 0));
        assertEquals(72, appointmentService.reminderLeadTimeHours(a));
    }

    @Test
    void reminderLeadTimeForRadiologyAfternoon() {
        Appointment a = new Appointment(1L, "Dr S", "radiology",
                LocalDateTime.of(2026, 11, 1, 14, 0));
        assertEquals(24, appointmentService.reminderLeadTimeHours(a));
    }

    @Test
    void reminderLeadTimeForGeneralMidday() {
        Appointment a = new Appointment(1L, "Dr S", "general",
                LocalDateTime.of(2026, 11, 1, 12, 0));
        assertEquals(12, appointmentService.reminderLeadTimeHours(a));
    }
}
