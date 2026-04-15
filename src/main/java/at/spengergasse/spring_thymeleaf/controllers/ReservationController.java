package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller für Reservierungen.
 * Behandelt Anzeigen der Liste, Formular zum Anlegen und die Erstellung (mit Validierung).
 */
@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

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
        try {
            log.info("Loading reservation form");
            Reservation reservation = new Reservation();
            // wichtige Initialisierung für Nested-Binding in Thymeleaf (patient.id, geraet.id)
            reservation.setPatient(new Patient());
            reservation.setGeraet(new Geraete());
            model.addAttribute("reservation", reservation);

            log.debug("Fetching patients");
            model.addAttribute("patients", patientRepository.findAll());

            log.debug("Fetching geraete");
            model.addAttribute("geraete", geraeteRepository.findAll());

            log.info("Reservation form loaded successfully");
            return "add_reservation";
        } catch (Exception e) {
            log.error("Error loading reservation form", e);
            throw new RuntimeException("Failed to load reservation form: " + e.getMessage(), e);
        }
    }

    /**
     * POST-Handler zum Speichern einer neuen Reservierung.
     * Empfängt einfache Form-Parameter: patientId, geraeteId, startTime, endTime, region, comment
     */
    @PostMapping("/add")
    public String addPost(
            @RequestParam(required = false) Integer patientId,
            @RequestParam(required = false) Integer geraeteId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String comment,
            Model model) {
        try {
            log.debug("Creating reservation: patientId={}, geraeteId={}, startTime={}, endTime={}",
                    patientId, geraeteId, startTime, endTime);

            // Validierung
            if (patientId == null || patientId <= 0) {
                model.addAttribute("error", "Bitte einen Patienten auswählen.");
                model.addAttribute("patients", patientRepository.findAll());
                model.addAttribute("geraete", geraeteRepository.findAll());
                return "add_reservation";
            }
            if (geraeteId == null || geraeteId <= 0) {
                model.addAttribute("error", "Bitte ein Gerät auswählen.");
                model.addAttribute("patients", patientRepository.findAll());
                model.addAttribute("geraete", geraeteRepository.findAll());
                return "add_reservation";
            }

            // Lade Patient und Gerät aus Datenbank
            Patient patient = patientRepository.findById(patientId).orElse(null);
            Geraete geraete = geraeteRepository.findById(geraeteId).orElse(null);

            if (patient == null || geraete == null) {
                model.addAttribute("error", "Patient oder Gerät nicht gefunden.");
                model.addAttribute("patients", patientRepository.findAll());
                model.addAttribute("geraete", geraeteRepository.findAll());
                return "add_reservation";
            }

            // Erstelle Reservierung
            Reservation reservation = new Reservation();
            reservation.setPatient(patient);
            reservation.setGeraet(geraete);
            reservation.setRegion(region);
            reservation.setComment(comment);

            // Parse DateTime strings (format: yyyy-MM-ddTHH:mm from HTML5 datetime-local)
            if (startTime != null && !startTime.isEmpty()) {
                reservation.setStartTime(LocalDateTime.parse(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                reservation.setEndTime(LocalDateTime.parse(endTime));
            }

            // Speichern
            reservationRepository.save(reservation);
            log.info("Reservation created successfully: {}", reservation.getId());
            return "redirect:/reservation/list";

        } catch (Exception e) {
            log.error("Error creating reservation", e);
            model.addAttribute("error", "Fehler beim Erstellen der Reservierung: " + e.getMessage());
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("geraete", geraeteRepository.findAll());
            return "add_reservation";
        }
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
