package at.spengergasse.spring_thymeleaf.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

/**
 * Entity-Klasse für einen Patienten.
 * Repräsentiert einen Datensatz in der Patient-Tabelle der Datenbank.
 */
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Sozialversicherungsnummer (SVNR)
    private String ssn;
    // Vorname des Patienten
    private String firstName;
    // Nachname des Patienten
    private String lastName;
    // Geschlecht (z.B. "M", "F", "D")
    private String gender;
    // Geburtsdatum
    private LocalDate birth;

    // Standard-Getter/Setter - werden von Spring Data / JPA verwendet.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    /**
     * Hilfsmethode zur Anzeige eines Namens in Templates.
     * Falls Nachname leer ist, wird nur Vorname zurückgegeben.
     */
    public String getName() {
        return firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
    }
}
