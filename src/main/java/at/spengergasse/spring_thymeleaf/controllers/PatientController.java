package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Patient;
import at.spengergasse.spring_thymeleaf.entities.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/patient")
public class PatientController {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/list")
    public String patients(Model model) {
        try {
            model.addAttribute("patients", patientRepository.findAll());
            return "patlist";
        } catch (TransactionException ex) {
            model.addAttribute("message", "🔴 Datenbankfehler: Die Patientenliste konnte nicht geladen werden. Bitte prüfen Sie, ob MySQL läuft.");
            return "error";
        }
    }

    @GetMapping("/add")
    public String addPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "add_patient";
    }

    @PostMapping("/add")
    public String addPatient(@ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model) {
        try {
            // Validiere die Eingaben (BindingResult)
            if (bindingResult.hasErrors()) {
                model.addAttribute("message", "⚠️ Validierungsfehler: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
                return "error";
            }

            // Speichere den Patienten (wirft IllegalArgumentException bei Validierungsfehlern)
            patientRepository.save(patient);
            return "redirect:/patient/list";

        } catch (IllegalArgumentException ex) {
            // Fehler aus den Settern (z.B. SVNR-Validierung, Geburtsdatum-Validierung, "admin"-Validierung)
            // Diese Exception kommt von Patient.setSsn(), Patient.setBirth(), etc.
            String errorMessage = ex.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Ein Validierungsfehler ist aufgetreten";
            }
            model.addAttribute("message", "⚠️ Validierungsfehler: " + errorMessage);
            System.out.println("IllegalArgumentException abgefangen: " + errorMessage);
            return "error";

        } catch (TransactionException ex) {
            // Datenbankverbindungsfehler
            model.addAttribute("message", "🔴 Datenbankfehler: Der Patient konnte nicht gespeichert werden. Bitte prüfen Sie, ob MySQL läuft.");
            return "error";

        } catch (Exception ex) {
            // Alle anderen Fehler
            String errorMessage = ex.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Ein unbekannter Fehler ist aufgetreten";
            }
            model.addAttribute("message", "❌ Ein unerwarteter Fehler ist aufgetreten: " + errorMessage);
            System.err.println("Fehler beim Speichern des Patienten: " + ex.getClass().getName() + " - " + errorMessage);
            ex.printStackTrace();
            return "error";
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        // Fehler aus den Settern (SVNR, Geburtsdatum, Admin-Name)
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Ein Validierungsfehler ist aufgetreten";
        }
        model.addAttribute("message", "⚠️ Validierungsfehler: " + errorMessage);
        System.out.println("IllegalArgumentException Handler: " + errorMessage);
        return "error";
    }

    @ExceptionHandler(TransactionException.class)
    public String handleTransactionException(TransactionException ex, Model model) {
        // Datenbankverbindungsfehler
        model.addAttribute("message", "🔴 Datenbankfehler: Der Datenbankzugriff funktioniert nicht. Bitte prüfen Sie, ob MySQL läuft.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        // Alle anderen Fehler
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Ein unbekannter Fehler ist aufgetreten";
        }

        // Überprüfe auf spezifische Exception-Typen
        if (ex instanceof IllegalArgumentException) {
            model.addAttribute("message", "⚠️ Validierungsfehler: " + errorMessage);
        } else if (ex instanceof TransactionException) {
            model.addAttribute("message", "🔴 Datenbankfehler: Der Datenbankzugriff funktioniert nicht. Bitte prüfen Sie, ob MySQL läuft.");
        } else {
            model.addAttribute("message", "❌ Ein unerwarteter Fehler ist aufgetreten: " + errorMessage);
        }

        System.err.println("Exception Handler: " + ex.getClass().getName() + " - " + errorMessage);
        ex.printStackTrace();
        return "error";
    }
}
