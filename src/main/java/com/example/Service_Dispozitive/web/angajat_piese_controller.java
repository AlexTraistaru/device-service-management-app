/** Clasa pentru gestionarea pieselor (crud) si raportul de reaprovizionare.
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.piesa_acces;
import com.example.Service_Dispozitive.acces_date.raport_acces;
import com.example.Service_Dispozitive.entitati.piesa;
import com.example.Service_Dispozitive.entitati.raport_piesa_reaprovizionare;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/angajat/piese")
// controller pentru lista/adauga/editeaza/sterge piese + raport stoc
public class angajat_piese_controller {

    private final piesa_acces piese;
    private final raport_acces rapoarte;

    public angajat_piese_controller(piesa_acces piese, raport_acces rapoarte) {
        this.piese = piese;
        this.rapoarte = rapoarte;
    }

    // get /angajat/piese
    // afiseaza lista pieselor + (optional) mesaj/eroare
    @GetMapping
    public String lista(Model model,
                        @RequestParam(required = false) String mesaj,
                        @RequestParam(required = false) String eroare) {

        model.addAttribute("lista", piese.findAll());
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        return "angajat_piese";
    }

    // get /angajat/piese/adauga
    // afiseaza formular gol pentru adaugare
    @GetMapping("/adauga")
    public String paginaAdauga(Model model) {
        model.addAttribute("p", new piesa());
        model.addAttribute("titlu", "Adauga piesa");
        model.addAttribute("actiune", "/angajat/piese/adauga");
        return "angajat_piesa_form";
    }

    // post /angajat/piese/adauga
    // salveaza piesa noua
    @PostMapping("/adauga")
    public String adauga(@RequestParam String denumire,
                         @RequestParam String cod,
                         @RequestParam BigDecimal pret,
                         @RequestParam String furnizor,
                         @RequestParam int stoc) {

        // validare minima
        List<String> erori = new ArrayList<>();
        if (denumire == null || denumire.trim().isEmpty()) erori.add("Denumirea este obligatorie.");
        if (cod == null || cod.trim().isEmpty()) erori.add("Codul este obligatoriu.");
        if (pret == null || pret.signum() <= 0) erori.add("Pretul trebuie sa fie pozitiv.");
        if (stoc < 0) erori.add("Stocul nu poate fi negativ.");

        if (!erori.isEmpty()) {
            return "redirect:/angajat/piese?eroare=Date%20invalide";
        }

        piese.insert(denumire.trim(), cod.trim(), pret, furnizor == null ? null : furnizor.trim(), stoc);
        return "redirect:/angajat/piese?mesaj=Piesa%20adaugata";
    }

    // get /angajat/piese/{id}/edit
    // afiseaza formular cu datele piesei
    @GetMapping("/{id}/edit")
    public String paginaEdit(@PathVariable int id, Model model) {
        piesa p = piese.findById(id);
        if (p == null) return "redirect:/angajat/piese?eroare=Piesa%20inexistenta";

        model.addAttribute("p", p);
        model.addAttribute("titlu", "Editeaza piesa");
        model.addAttribute("actiune", "/angajat/piese/" + id + "/edit");
        return "angajat_piesa_edit";
    }

    // post /angajat/piese/{id}/edit
    // salveaza modificarile piesei
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable int id,
                       @RequestParam String denumire,
                       @RequestParam String cod,
                       @RequestParam BigDecimal pret,
                       @RequestParam String furnizor,
                       @RequestParam int stoc) {

        // validare minima
        if (denumire == null || denumire.trim().isEmpty()) {
            return "redirect:/angajat/piese?eroare=Denumire%20obligatorie";
        }
        if (cod == null || cod.trim().isEmpty()) {
            return "redirect:/angajat/piese?eroare=Cod%20obligatoriu";
        }
        if (pret == null || pret.signum() <= 0) {
            return "redirect:/angajat/piese?eroare=Pret%20invalid";
        }
        if (stoc < 0) {
            return "redirect:/angajat/piese?eroare=Stoc%20invalid";
        }

        piese.update(id, denumire.trim(), cod.trim(), pret, furnizor == null ? null : furnizor.trim(), stoc);
        return "redirect:/angajat/piese?mesaj=Piesa%20actualizata";
    }

    // get /angajat/piese/raport
    // raport: piese sub prag + consum in ultimele n zile
    @GetMapping("/raport")
    public String raport(Model model,
                         @RequestParam(required = false) Integer pragStoc,
                         @RequestParam(required = false) Integer zile) {

        int prag = (pragStoc == null) ? 5 : pragStoc;
        int z = (zile == null) ? 30 : zile;

        List<raport_piesa_reaprovizionare> alerte = rapoarte.pieseSubPragCuConsum(prag, z);

        // lista pieselor (ca pagina sa arate complet)
        model.addAttribute("lista", piese.findAll());

        // datele pentru formular (sa ramana completate)
        model.addAttribute("pragStoc", prag);
        model.addAttribute("zile", z);

        // exact ce asteapta angajat_piese.html
        model.addAttribute("alerte", alerte);

        return "angajat_piese";
    }


    // post /angajat/piese/{id}/sterge
    // sterge o piesa doar daca nu e folosita in comenzi
    @PostMapping("/{id}/sterge")
    public String sterge(@PathVariable int id) {
        if (piese.esteFolositaInComenzi(id)) {
            return "redirect:/angajat/piese?eroare=Nu%20poti%20sterge%20o%20piesa%20folosita%20in%20comenzi";
        }

        try {
            piese.delete(id);
        } catch (Exception ex) {
            return "redirect:/angajat/piese?eroare=Eroare%20la%20stergere";
        }
        return "redirect:/angajat/piese?mesaj=Piesa%20stearsa";
    }
}
