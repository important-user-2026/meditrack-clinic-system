package com.meditrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * A patient registered with the clinic.
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    private String name;

    @Email(message = "A valid email is required")
    private String email;

    private String phone;

    // NHS / clinic reference number
    private String nhsNumber;

    private int age;

    public Patient() {
        // JPA requires a no-args constructor
    }

    public Patient(String name, String email, String phone, String nhsNumber, int age) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nhsNumber = nhsNumber;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * A patient counts as a minor if under 18. Used to flag records that
     * need guardian consent before an appointment is confirmed.
     */
    public boolean isMinor() {
        return age < 18;
    }
}
