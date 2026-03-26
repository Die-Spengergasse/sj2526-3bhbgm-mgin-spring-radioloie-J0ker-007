package at.spengergasse.spring_thymeleaf.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity für ein Untersuchungsgerät.
 * Felder: bezeichnung (z.B. "MR-1"), art (z.B. "MR"), standort (Raumnummer).
 */
@Entity
public class Geraete  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Bezeichnung/Name des Geräts
    private String bezeichnung;
    // Art des Gerätes (z.B. MR, CT, Roentgen)
    private String art;
    // Standort/Raumnummer
    private String standort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }
}
