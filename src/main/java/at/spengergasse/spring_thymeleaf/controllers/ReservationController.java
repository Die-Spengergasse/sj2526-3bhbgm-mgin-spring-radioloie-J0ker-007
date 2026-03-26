package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller für Reservierungen.
 * Behandelt Anzeigen der Liste, Formular zum Anlegen und die Erstellung (mit Validierung).
 */
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final PatientRepository patientRepository;
    private final GeraeteRepository geraeteRepository;

    public ReservationController(ReservationRepository reservationRepository, PatientRepository patientRepository, GeraeteRepository geraeteRepository) {
        this.reservationRepository = reservationRepository;
        this.patientRepository = patientRepository;
        this.geraeteRepository = geraeteRepository;
    }

    /**
     * Zeigt alle Reservierungen an.
     */
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        return "reslist";
    }

    /**
     * Zeigt Reservierungen für ein bestimmtes Gerät (nach id).
     */
    @GetMapping("/list/geraet/{id}")
    public String listByGeraet(@PathVariable("id") int id, Model model) {
        model.addAttribute("reservations", reservationRepository.findByGeraetId(id));
        geraeteRepository.findById(id).ifPresent(g -> model.addAttribute("geraet", g));
        return "reslist_by_geraet";
    }

    /**
     * GET-Handler zum Anzeigen des Formulars zum Anlegen einer Reservierung.
     * Wir initialisieren `reservation.patient` und `reservation.geraet`, damit Thymeleaf
     * die gebundenen Felder `patient.id` und `geraet.id` ohne NullPointer rendern kann.
     */
    @GetMapping("/add")
    public String add(Model model) {
        Reservation reservation = new Reservation();
        // wichtige Initialisierung für Nested-Binding in Thymeleaf (patient.id, geraet.id)
        reservation.setPatient(new Patient());
        reservation.setGeraet(new Geraete());
        model.addAttribute("reservation", reservation);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("geraete", geraeteRepository.findAll());
        return "add_reservation";
    }

    /**
     * POST-Handler zum Speichern einer neuen Reservierung.
     * - @Valid löst bean-validation aus
     * - BindingResult enthält Validierungsfehler
     * - Wir prüfen zusätzlich auf zeitliche Überlappungen für das gleiche Gerät
     */
    @PostMapping("/add")
    public String addPost(@Valid @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult, Model model) {
        // lade verknüpfte Entitäten anhand der übermittelten IDs (Selects binden nur die IDs)
        if (reservation.getPatient() != null && reservation.getPatient().getId() != 0) {
            patientRepository.findById(reservation.getPatient().getId()).ifPresent(reservation::setPatient);
        }
        if (reservation.getGeraet() != null && reservation.getGeraet().getId() != 0) {
            geraeteRepository.findById(reservation.getGeraet().getId()).ifPresent(reservation::setGeraet);
        }

        // Wenn Validierungsfehler vorliegen, Formular neu anzeigen (mit Fehlern)
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("geraete", geraeteRepository.findAll());
            return "add_reservation";
        }

        // Konfliktprüfung: Gibt es eine überlappende Reservierung für dasselbe Gerät?
        List<Reservation> existing = reservationRepository.findByGeraetId(reservation.getGeraet().getId());
        boolean overlap = existing.stream().anyMatch(r -> {
            // keine Überlappung, wenn Ende vor Start des anderen ist oder Start nach Ende des anderen
            return !(reservation.getEndTime().isBefore(r.getStartTime()) || reservation.getStartTime().isAfter(r.getEndTime()));
        });

        if (overlap) {
            // Globaler Fehler (nicht feldbezogen) - wird im Template angezeigt
            bindingResult.reject("overlap", "Die Reservierung überlappt sich mit einer bestehenden Reservierung für dieses Gerät.");
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("geraete", geraeteRepository.findAll());
            return "add_reservation";
        }

        // Speichern, wenn alles ok ist
        reservationRepository.save(reservation);
        return "redirect:/reservation/list";
    }

    // Platzhalter-Handler für Edit/Delete/Details — noch nicht implementiert
    @RequestMapping("/edit")
    public String edit() {
        return "edit_reservation";
    }

    @RequestMapping("/delete")
    public String delete() {
        return "delete_reservation";
    }

    @RequestMapping("/details")
    public String details() {
        return "resdetails";
    }

}
