package at.spengergasse.spring_thymeleaf.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entity für eine Reservierung (Untersuchungstermin auf einem Gerät)
 * Enthält Verweise auf Patient und Geraet, sowie Start-/Endzeit, Region und Kommentar.
 */
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Referenz zum Patienten (ManyToOne)
    @ManyToOne
    @NotNull
    private Patient patient;

    // Referenz zum Gerät (ManyToOne)
    @ManyToOne
    @NotNull
    private Geraete geraet;

    // Start- und Endzeit der Reservierung
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;

    // Untersuchungregion (z.B. Kopf, Thorax)
    @Size(max = 255)
    private String region;

    // Freitext-Kommentar
    @Size(max = 2000)
    private String comment;

    // Standard-Getter/Setter ...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Geraete getGeraet() {
        return geraet;
    }

    public void setGeraet(Geraete geraet) {
        this.geraet = geraet;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
