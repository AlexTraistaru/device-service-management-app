/** Clasa pentru gestionarea serviciilor (crud) de catre angajat.
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.serviciu_acces;
import com.example.Service_Dispozitive.entitati.serviciu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/angajat/servicii")
// controller pentru lista/adauga/editeaza/sterge servicii
public class angajat_servicii_controller {

    private final serviciu_acces servicii;

    public angajat_servicii_controller(serviciu_acces servicii) {
        this.servicii = servicii;
    }

    // get /angajat/servicii
    // afiseaza lista serviciilor
    @GetMapping
    public String lista(Model model,
                        @RequestParam(required = false) String mesaj,
                        @RequestParam(required = false) String eroare) {

        model.addAttribute("lista", servicii.findAll());
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        return "angajat_servicii";
    }

    // get /angajat/servicii/adauga
    // afiseaza formular gol
    @GetMapping("/adauga")
    public String paginaAdauga(Model model) {
        model.addAttribute("s", new serviciu());
        model.addAttribute("titlu", "Adauga serviciu");
        model.addAttribute("actiune", "/angajat/servicii/adauga");
        return "angajat_serviciu_form";
    }

    // post /angajat/servicii/adauga
    // salveaza serviciu nou
    @PostMapping("/adauga")
    public String adauga(@RequestParam String denumire,
                         @RequestParam BigDecimal pret) {

        List<String> erori = new ArrayList<>();

        if (denumire == null || denumire.trim().isEmpty()) erori.add("Denumirea este obligatorie.");
        if (pret == null || pret.signum() <= 0) erori.add("Pretul trebuie sa fie pozitiv.");

        if (!erori.isEmpty()) {
            return "redirect:/angajat/servicii?eroare=Date%20invalide";
        }

        servicii.insert(denumire.trim(), pret);
        return "redirect:/angajat/servicii?mesaj=Serviciu%20adaugat";
    }

    // get /angajat/servicii/{id}/edit
    // afiseaza formular cu datele serviciului
    @GetMapping("/{id}/edit")
    public String paginaEdit(@PathVariable int id, Model model) {
        serviciu s = servicii.findById(id);
        if (s == null) return "redirect:/angajat/servicii?eroare=Serviciu%20inexistent";

        model.addAttribute("s", s);
        model.addAttribute("titlu", "Editeaza serviciu");
        model.addAttribute("actiune", "/angajat/servicii/" + id + "/edit");
        return "angajat_serviciu_edit";
    }

    // post /angajat/servicii/{id}/edit
    // salveaza modificarile
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable int id,
                       @RequestParam String denumire,
                       @RequestParam BigDecimal pret) {

        if (denumire == null || denumire.trim().isEmpty()) {
            return "redirect:/angajat/servicii?eroare=Denumire%20obligatorie";
        }
        if (pret == null || pret.signum() <= 0) {
            return "redirect:/angajat/servicii?eroare=Pret%20invalid";
        }

        servicii.update(id, denumire.trim(), pret);
        return "redirect:/angajat/servicii?mesaj=Serviciu%20actualizat";
    }

    // post /angajat/servicii/{id}/sterge
    // sterge doar daca nu este folosit in comenzi
    @PostMapping("/{id}/sterge")
    public String sterge(@PathVariable int id) {
        if (servicii.esteFolositInComenzi(id)) {
            return "redirect:/angajat/servicii?eroare=Nu%20poti%20sterge%20un%20serviciu%20folosit%20in%20comenzi";
        }

        try {
            servicii.delete(id);
        } catch (Exception ex) {
            return "redirect:/angajat/servicii?eroare=Eroare%20la%20stergere";
        }
        return "redirect:/angajat/servicii?mesaj=Serviciu%20sters";
    }
}
