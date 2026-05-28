package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Geraete;
import at.spengergasse.spring_thymeleaf.entities.GeraeteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller für Gerät-Verwaltung (Medizinische Geräte wie MR, CT etc.)
 * Behandelt: Anzeigen der Liste, Formular zum Anlegen, Speichern neuer Geräte
 */
@Controller
@RequestMapping("/geraete")
public class GeraeteController {
    // Das Repository wird per Dependency Injection eingefügt (Konstruktor)
    // Es ermöglicht uns, mit der Datenbank zu kommunizieren
    private final GeraeteRepository geraeteRepository;

    /**
     * Konstruktor: Spring injiziert das GeraeteRepository automatisch
     */
    public GeraeteController(GeraeteRepository geraeteRepository) {
        this.geraeteRepository = geraeteRepository;
    }

    /**
     * GET /geraete/list - Zeigt eine Liste aller verfügbaren Geräte
     * Das Model wird mit der Liste gefüllt und an das Thymeleaf-Template "gerlist.html" übergeben
     */
    @GetMapping("/list")
    public String list(Model model) {
        try {
            model.addAttribute("geraete", geraeteRepository.findAll());
            return "gerlist"; // Render template: gerlist.html
        } catch (TransactionException ex) {
            model.addAttribute("message", "🔴 Datenbankfehler: Die Geräteliste konnte nicht geladen werden. Bitte prüfen Sie, ob MySQL läuft.");
            return "error";
        }
    }

    /**
     * GET /geraete/add - Zeigt das Formular zum Anlegen eines neuen Geräts
     * Ein neues Geraete-Objekt wird erstellt und an das Template übergeben
     */
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("geraet", new Geraete());
        return "add_geraet"; // Render template: add_geraet.html
    }

    /**
     * POST /geraete/add - Speichert ein neues Gerät in der Datenbank
     * @ModelAttribute bindet die Formular-Felder an ein Geraete-Objekt
     * Nach dem Speichern: Redirect zur Liste (/geraete/list)
     */
    @PostMapping("/add")
    public String addPost(@ModelAttribute("geraet") Geraete geraet, Model model) {
        try {
            geraeteRepository.save(geraet); // Speichere in DB
            return "redirect:/geraete/list"; // Redirect zu /geraete/list nach erfolgreichem Speichern

        } catch (IllegalArgumentException ex) {
            // Fehler aus den Settern
            model.addAttribute("message", "⚠️ Validierungsfehler: " + ex.getMessage());
            model.addAttribute("geraet", geraet);
            return "error";

        } catch (TransactionException ex) {
            // Datenbankverbindungsfehler
            model.addAttribute("message", "🔴 Datenbankfehler: Das Gerät konnte nicht gespeichert werden. Bitte prüfen Sie, ob MySQL läuft.");
            model.addAttribute("geraet", geraet);
            return "error";

        } catch (Exception ex) {
            // Andere Fehler
            model.addAttribute("message", "❌ Ein unerwarteter Fehler ist aufgetreten: " + ex.getMessage());
            model.addAttribute("geraet", geraet);
            System.err.println("Fehler beim Speichern des Geräts: " + ex.getMessage());
            ex.printStackTrace();
            return "error";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        // Datenbankverbindungsfehler
        if (ex instanceof TransactionException) {
            model.addAttribute("message", "🔴 Datenbankfehler: Der Datenbankzugriff funktioniert nicht. Bitte prüfen Sie, ob MySQL läuft.");
        }
        // Validierungsfehler
        else if (ex instanceof IllegalArgumentException) {
            model.addAttribute("message", "⚠️ Validierungsfehler: " + ex.getMessage());
        }
        // Alle anderen Fehler
        else {
            model.addAttribute("message", "❌ Ein Fehler ist aufgetreten: " + ex.getMessage());
        }
        return "error";
    }

}
