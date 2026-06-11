package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final GeraeteRepository geraeteRepository;
    private final PatientRepository patientRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 GeraeteRepository geraeteRepository,
                                 PatientRepository patientRepository) {
        this.reservationRepository = reservationRepository;
        this.geraeteRepository = geraeteRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("geraete", geraeteRepository.findAll());
        model.addAttribute("patients", patientRepository.findAll());
        return "add_reservation";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("reservation") Reservation reservation,
                      @RequestParam int patientId,
                      @RequestParam int geraeteId,
                      BindingResult result) throws Exception {

            // Validiere Binding Errors
            if (result.hasErrors()) {
                throw new Exception(result.getAllErrors().get(0).getDefaultMessage());
            }

            List<Reservation> existingReservations = reservationRepository.findByPatientId(patientId);
            LocalDateTime newStart = reservation.getStartTime();
            LocalDateTime newEnd = reservation.getEndTime();

            // Validiere, dass Startzeit nicht nach Endzeit liegt
            if (newStart != null && newEnd != null && newStart.isAfter(newEnd)) {
                throw new IllegalArgumentException("Die Startzeit darf nicht nach der Endzeit liegen.");
            }

            // Validiere, dass Termin nicht in Vergangenheit liegt
            if (newStart != null && newStart.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Ein Termin in der Vergangenheit darf nicht reserviert werden.");
            }

            // Prüfe auf Überschneidungen für den Patienten
            List<Reservation> patientReservations = reservationRepository.findByPatientId(patientId);
            for (Reservation r : patientReservations) {
                LocalDateTime existingStart = r.getStartTime();
                LocalDateTime existingEnd = r.getEndTime();

                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    throw new IllegalArgumentException("Ein Patient kann nicht zur gleichen Zeit mehrere Termine haben. Es gibt bereits eine Reservierung in diesem Zeitfenster.");
                }
            }

            Patient patient = patientRepository.findById(patientId);
            List<Reservation> geraeteReservations = reservationRepository.findByGeraetId(geraeteId);

            for (Reservation r : geraeteReservations) {
                LocalDateTime existingStart = r.getStartTime();
                LocalDateTime existingEnd = r.getEndTime();

                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    throw new IllegalArgumentException("Das Gerät ist zur gleichen Zeit bereits reserviert. Es gibt bereits einen Termin in diesem Zeitfenster.");
                }
            }

            Geraete geraete = geraeteRepository.findById(geraeteId);

            reservation.setPatient(patient);
            reservation.setGeraet(geraete);

            reservationRepository.save(reservation);

            return "redirect:/reservation/list";

    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        model.addAttribute("geraete", geraeteRepository.findAll());
        return "reslist";
    }

    @GetMapping("/listmachine")
    public String listmachine(@RequestParam int geraeteId, Model model) {

        List<Reservation> reservations = reservationRepository.findByGeraetId(geraeteId);

        model.addAttribute("reservations", reservations);

        return "reslist_by_geraet";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        if (ex instanceof TransactionException) {
            model.addAttribute("message", "Datenbankfehler: Der Datenbankzugriff funktioniert nicht.");
        } else if (ex instanceof IllegalArgumentException) {
            model.addAttribute("message", "Validierungsfehler: " + ex.getMessage());
        }
        return "error";
    }
}