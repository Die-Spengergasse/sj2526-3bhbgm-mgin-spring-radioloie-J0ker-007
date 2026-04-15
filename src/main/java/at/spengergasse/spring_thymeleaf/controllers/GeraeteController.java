package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Geraete;
import at.spengergasse.spring_thymeleaf.entities.GeraeteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("geraete", geraeteRepository.findAll());
        return "gerlist"; // Render template: gerlist.html
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
    public String addPost(@ModelAttribute("geraet") Geraete geraet) {
        geraeteRepository.save(geraet); // Speichere in DB
        return "redirect:/geraete/list"; // Redirect zu /geraete/list nach erfolgreichem Speichern
    }

    /**
     * GET /geraete/edit - Platzhalter zum Bearbeiten eines Geräts (nicht implementiert)
     */
    @RequestMapping("/edit")
    public String edit() {
        return "edit_geraet";
    }

    /**
     * GET /geraete/delete - Platzhalter zum Löschen eines Geräts (nicht implementiert)
     */
    @RequestMapping("/delete")
    public String delete() {
        return "delete_geraet";
    }

    /**
     * GET /geraete/details - Platzhalter zur Anzeige von Gerät-Details (nicht implementiert)
     */
    @RequestMapping("/details")
    public String details() {
        return "getdetails";
    }

}
