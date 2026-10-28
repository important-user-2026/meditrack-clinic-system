package com.meditrack.controller;

import com.meditrack.model.Appointment;
import com.meditrack.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> forPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    @PostMapping
    public ResponseEntity<Appointment> book(@RequestBody Appointment appointment) {
        Appointment saved = appointmentService.bookAppointment(appointment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/confirm")
    public Appointment confirm(@PathVariable Long id) {
        return appointmentService.confirmAppointment(id);
    }

    @PutMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }
}
