package at.spengergasse.spring_thymeleaf.controllers;

import at.spengergasse.spring_thymeleaf.entities.Geraete;
import at.spengergasse.spring_thymeleaf.entities.GeraeteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/geraete")
public class GeraeteController {
    private final GeraeteRepository geraeteRepository;

    public GeraeteController(GeraeteRepository geraeteRepository) {
        this.geraeteRepository = geraeteRepository;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("geraete", geraeteRepository.findAll());
        return "gerlist";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("geraet", new Geraete());
        return "add_geraet";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("geraet") Geraete geraet) {
        geraeteRepository.save(geraet);
        return "redirect:/geraete/list";
    }

    @RequestMapping("/edit")
    public String edit() {
        return "edit_geraet";
    }

    @RequestMapping("/delete")
    public String delete() {
        return "delete_geraet";
    }

    @RequestMapping("/details")
    public String details() {
        return "getdetails";
    }

}
