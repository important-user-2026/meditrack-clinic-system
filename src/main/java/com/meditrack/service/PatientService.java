package com.meditrack.service;

import com.meditrack.exception.ResourceNotFoundException;
import com.meditrack.model.Patient;
import com.meditrack.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic for registering and retrieving patients.
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    public Patient registerPatient(Patient patient) {
        // --- Validation block (intentionally duplicated in AppointmentService
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

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    /**
     * Categorises a patient into an age band. Written with magic numbers and a
     * long if/else chain on purpose - SonarQube flags the magic numbers and
     * the raised cognitive complexity as code smells.
     */
    public String ageBand(Patient patient) {
        int a = patient.getAge();
        String band;
        if (a < 1) {
            band = "infant";
        } else if (a < 13) {
            band = "child";
        } else if (a < 18) {
            band = "adolescent";
        } else if (a < 40) {
            band = "young-adult";
        } else if (a < 65) {
            band = "adult";
        } else if (a < 80) {
            band = "senior";
        } else {
            band = "elderly";
        }
        return band;
    }
}
