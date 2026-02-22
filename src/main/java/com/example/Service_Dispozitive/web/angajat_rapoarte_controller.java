/** Clasa pentru pagina cu rapoarte (interogari complexe) pentru angajat.
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.departament_acces;
import com.example.Service_Dispozitive.acces_date.raport_acces;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/angajat/rapoarte")
// controller pentru rularea rapoartelor si afisarea rezultatelor
public class angajat_rapoarte_controller {

    private final raport_acces rapoarte;
    private final departament_acces departamente;

    public angajat_rapoarte_controller(raport_acces rapoarte, departament_acces departamente) {
        this.rapoarte = rapoarte;
        this.departamente = departamente;
    }

    // pagina initiala (fara rezultate)
    @GetMapping
    public String pagina(Model model,
                         @RequestParam(required = false) String eroare,
                         @RequestParam(required = false) String mesaj) {

        // lista pentru dropdown la rapoarte
        model.addAttribute("departamente", departamente.findAll());

        // mesaje optionale
        model.addAttribute("eroare", eroare);
        model.addAttribute("mesaj", mesaj);

        return "angajat_rapoarte";
    }

    @GetMapping("/angajat-disponibil")
    public String raportAngajatDisponibil(
            @RequestParam int departamentId,
            Model model
    ) {
        // reincarcam mereu lista de departamente, ca sa apara dropdown-ul si dupa afisare
        model.addAttribute("departamente", departamente.findAll());

        // tinem minte ce departament a ales utilizatorul (ca sa ramana selectat)
        model.addAttribute("departamentSelectat", departamentId);

        // executam raportul 1 din raport_acces
        model.addAttribute("rezAngajatDisponibil", rapoarte.angajatCelMaiDisponibil(departamentId));

        // ramanem pe aceeasi pagina de rapoarte
        return "angajat_rapoarte";
    }

    @GetMapping("/comenzi-fara-factura")
    public String raportComenziFaraFactura(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataLimita,
            Model model
    ) {
        // reincarcam mereu lista de departamente, ca pagina sa arate complet dupa submit
        model.addAttribute("departamente", departamente.findAll());

        // tinem minte data aleasa (ca sa ramana afisata in input)
        model.addAttribute("dataLimita", dataLimita);

        // executam raportul 2 din raport_acces
        model.addAttribute("rezComenziFaraFactura", rapoarte.comenziFaraFacturaPanaLa(dataLimita));

        // ramanem pe aceeasi pagina de rapoarte
        return "angajat_rapoarte";
    }


    // post /angajat/rapoarte/top-clienti
    // ruleaza raportul "top clienti" cu parametri din formular
    @GetMapping("/top-clienti")
    public String topClienti(Model model,
                             @RequestParam String dataStart,
                             @RequestParam String dataEnd,
                             @RequestParam String pragMinim) {

        // pastram departamentele in pagina si dupa submit (ca sa nu se goleasca dropdown-ul)
        model.addAttribute("departamente", departamente.findAll());

        try {
            // transformam string -> LocalDate / BigDecimal
            LocalDate ds = LocalDate.parse(dataStart);
            LocalDate de = LocalDate.parse(dataEnd);
            BigDecimal prag = new BigDecimal(pragMinim);

            // validare minima pentru interval
            if (de.isBefore(ds)) {
                model.addAttribute("eroare", "data end trebuie sa fie dupa data start");
                return "angajat_rapoarte";
            }

            // aici rulam interogarea complexa din raport_acces
            model.addAttribute("rezTopClienti", rapoarte.topClienti(ds, de, prag));

            // punem parametrii inapoi in pagina ca sa ramana completati
            model.addAttribute("dataStart", dataStart);
            model.addAttribute("dataEnd", dataEnd);
            model.addAttribute("pragMinim", pragMinim);

        } catch (Exception ex) {
            model.addAttribute("eroare", "Date/prag invalide. Datele trebuie YYYY-MM-DD, prag ex: 500 sau 500.50");
        }

        return "angajat_rapoarte";
    }
}
