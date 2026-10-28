package com.meditrack.service;

import com.meditrack.exception.ResourceNotFoundException;
import com.meditrack.model.Appointment;
import com.meditrack.model.AppointmentStatus;
import com.meditrack.model.Patient;
import com.meditrack.repository.AppointmentRepository;
import com.meditrack.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic for booking and managing appointments.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    // Unused field - SonarQube code smell
    private int bookingCounter;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public Appointment bookAppointment(Appointment appointment) {
        Patient patient = patientRepository.findById(appointment.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot book: patient not found: " + appointment.getPatientId()));

        // --- Validation block (intentionally duplicated from PatientService
        //     so SonarQube reports a duplication) ---
        if (patient == null) {
            throw new IllegalArgumentException("Patient must not be null");
        }
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required");
        }
        if (patient.getAge() < 0 || patient.getAge() > 120) {
            throw new IllegalArgumentException("Patient age is out of range");
        }
        // --- end duplicated block ---

        // Debugging left in production code - SonarQube code smell
        System.out.println("Booking appointment for patient " + patient.getName());

        appointment.setStatus(AppointmentStatus.REQUESTED);
        return appointmentRepository.save(appointment);
    }

    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found: " + appointmentId));
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found: " + appointmentId));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Works out a reminder lead time (in hours) before an appointment.
     * Deliberately convoluted with magic numbers and nested conditions to
     * raise cognitive complexity - a SonarQube maintainability code smell.
     */
    public int reminderLeadTimeHours(Appointment appointment) {
        if (appointment == null || appointment.getScheduledFor() == null) {
            return 24;
        }
        LocalDateTime when = appointment.getScheduledFor();
        int hour = when.getHour();
        String dept = appointment.getDepartment();

        if (dept != null && dept.equalsIgnoreCase("surgery")) {
            if (hour < 9) {
                return 72;
            } else if (hour < 12) {
                return 48;
            } else {
                return 36;
            }
        } else if (dept != null && dept.equalsIgnoreCase("radiology")) {
            if (hour < 12) {
                return 48;
            } else {
                return 24;
            }
        } else {
            if (hour < 8) {
                return 24;
            } else if (hour < 18) {
                return 12;
            } else {
                return 6;
            }
        }
    }
}
