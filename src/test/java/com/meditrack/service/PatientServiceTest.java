package com.meditrack.service;

import com.meditrack.exception.ResourceNotFoundException;
import com.meditrack.model.Patient;
import com.meditrack.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient("Alice", "alice@clinic.com", "07123", "NHS123", 34);
        patient.setId(1L);
    }

    @Test
    void getPatientByIdReturnsPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        Patient found = patientService.getPatientById(1L);
        assertEquals("Alice", found.getName());
    }

    @Test
    void getPatientByIdThrowsWhenMissing() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatientById(99L));
    }

    @Test
    void registerPatientSavesValidPatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        Patient saved = patientService.registerPatient(patient);
        assertEquals("Alice", saved.getName());
    }

    @Test
    void registerPatientRejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(null));
    }

    @Test
    void registerPatientRejectsBlankName() {
        Patient bad = new Patient("  ", "x@x.com", "0", "NHS", 20);
        assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(bad));
    }

    @Test
    void registerPatientRejectsOutOfRangeAge() {
        Patient bad = new Patient("Bob", "b@x.com", "0", "NHS", 200);
        assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(bad));
    }

    @Test
    void getAllPatientsReturnsList() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        assertEquals(1, patientService.getAllPatients().size());
    }

    @Test
    void ageBandClassifiesCorrectly() {
        patient.setAge(0);
        assertEquals("infant", patientService.ageBand(patient));
        patient.setAge(8);
        assertEquals("child", patientService.ageBand(patient));
        patient.setAge(15);
        assertEquals("adolescent", patientService.ageBand(patient));
        patient.setAge(30);
        assertEquals("young-adult", patientService.ageBand(patient));
        patient.setAge(50);
        assertEquals("adult", patientService.ageBand(patient));
        patient.setAge(70);
        assertEquals("senior", patientService.ageBand(patient));
        patient.setAge(90);
        assertEquals("elderly", patientService.ageBand(patient));
    }
}
