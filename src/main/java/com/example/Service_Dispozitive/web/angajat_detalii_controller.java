/** Clasa pentru pagina de profil a angajatului (date personale + departament).
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.angajat_acces;
import com.example.Service_Dispozitive.acces_date.departament_acces;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import com.example.Service_Dispozitive.entitati.utilizator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/angajat")
// controller pentru afisare si salvare date angajat
public class angajat_detalii_controller {

    private final utlizator_acces utilizatori;
    private final angajat_acces angajati;
    private final departament_acces departamente;

    public angajat_detalii_controller(utlizator_acces utilizatori,
                                      angajat_acces angajati,
                                      departament_acces departamente) {
        this.utilizatori = utilizatori;
        this.angajati = angajati;
        this.departamente = departamente;
    }

    // get /detalii
    // afiseaza pagina cu datele angajatului + dropdown de departamente
    @GetMapping("/detalii")
    public String pagina(Authentication auth,
                         @RequestParam(required = false) String mesaj,
                         @RequestParam(required = false) String eroare,
                         Model model) {

        // luam utilizatorul logat
        utilizator u = utilizatori.findByNumeUtilizator(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        // luam datele angajatului (map, ca vine direct din sql)
        Map<String, Object> det = angajati.getDetaliiAngajatByUserId(u.getId());

        // punem in pagina: detalii + lista departamente
        model.addAttribute("det", det);
        model.addAttribute("departamente", departamente.findAll());

        // mesaje pentru user (optional)
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        return "angajat_detalii";
    }

    // post /detalii
    // salveaza modificarile facute in formular
    @PostMapping("/detalii")
    public String salveaza(Authentication auth,
                           @RequestParam String nume,
                           @RequestParam String prenume,
                           @RequestParam(required = false) String email,
                           @RequestParam int departamentId) {

        // luam utilizatorul logat
        utilizator u = utilizatori.findByNumeUtilizator(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        // verificare minima: nume si prenume obligatorii
        if (nume == null || nume.trim().isEmpty() || prenume == null || prenume.trim().isEmpty()) {
            return "redirect:/angajat/detalii?eroare=Nume%20si%20prenume%20sunt%20obligatorii";
        }

        // salvam in Angajati (pe userId)
        angajati.updateDetaliiAngajat(
                u.getId(),
                nume.trim(),
                prenume.trim(),
                (email == null || email.trim().isEmpty()) ? null : email.trim(),
                departamentId
        );

        return "redirect:/angajat/detalii?mesaj=Date%20angajat%20actualizate";
    }
}
