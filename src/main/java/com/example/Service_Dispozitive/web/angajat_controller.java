/** Clasa pentru fluxul principal al angajatului pe comenzi (lista, detalii, status, servicii, piese, factura).
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.angajat_acces;
import com.example.Service_Dispozitive.acces_date.comanda_angajat_acces;
import com.example.Service_Dispozitive.acces_date.factura_acces;
import com.example.Service_Dispozitive.acces_date.piesa_acces;
import com.example.Service_Dispozitive.acces_date.serviciu_acces;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import com.example.Service_Dispozitive.entitati.comanda_angajat_view;
import com.example.Service_Dispozitive.entitati.utilizator;
import com.example.Service_Dispozitive.service.angajat_comanda_service;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
// baza pentru rutele din controller: /angajat
@RequestMapping("/angajat")
// controller principal pentru actiunile angajatului pe comenzi
public class angajat_controller {

    // interogari join pentru lista/detalii comenzi
    private final comanda_angajat_acces comenzi;

    // catalog servicii
    private final serviciu_acces servicii;

    // catalog piese
    private final piesa_acces piese;

    // verificare factura existenta
    private final factura_acces facturi;

    // logica de business: start lucru, servicii executate, adauga piese, genereaza factura
    private final angajat_comanda_service service;

    // avem nevoie sa transformam "username" -> userID -> angajatID
    private final utlizator_acces utilizatori;
    private final angajat_acces angajati;

    public angajat_controller(comanda_angajat_acces comenzi,
                              serviciu_acces servicii,
                              piesa_acces piese,
                              factura_acces facturi,
                              angajat_comanda_service service,
                              utlizator_acces utilizatori,
                              angajat_acces angajati) {
        this.comenzi = comenzi;
        this.servicii = servicii;
        this.piese = piese;
        this.facturi = facturi;
        this.service = service;
        this.utilizatori = utilizatori;
        this.angajati = angajati;
    }

    // ia angajatID-ul din username-ul logat
    private Integer angajatIdDinAuth(Authentication auth) {
        String username = auth.getName();

        utilizator u = utilizatori.findByNumeUtilizator(username)
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent: " + username));

        // folosim metoda existenta din angajat_acces
        Integer angajatId = angajati.getAngajatIdByUserId(u.getId());

        // guard simplu daca nu exista rand in Angajati
        if (angajatId == null) {
            throw new IllegalStateException("Nu exista rand in Angajati pentru acest user.");
        }

        return angajatId;
    }

    // 1) comenzile mele (doar cele asignate angajatului logat)

    @GetMapping("/comenzile-mele")
    public String comenzileMele(Authentication auth,
                                @RequestParam(required = false) String mesaj,
                                @RequestParam(required = false) String eroare,
                                Model model) {

        // username-ul angajatului logat
        String username = auth.getName();

        // luam userul din tabela Utilizatori
        utilizator u = utilizatori.findByNumeUtilizator(username)
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent: " + username));

        // luam angajatId si departamentul curent al angajatului
        Integer angajatId = angajati.getAngajatIdByUserId(u.getId());
        Integer depId = angajati.getDepartamentIdByUserId(u.getId());

        // afisam doar comenzile din departamentul curent al angajatului
        model.addAttribute("lista", comenzi.listAleMeleInDepartament(angajatId, depId));
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        // text afisat in pagina
        model.addAttribute("titlu", "Comenzile mele (departamentul meu curent)");
        model.addAttribute("arataDetalii", true);

        return "angajat_comenzi";
    }

    // 2) comenzi totale (toate comenzile din service)

    @GetMapping("/comenzi-toate")
    public String comenziToate(@RequestParam(required = false) String mesaj,
                               @RequestParam(required = false) String eroare,
                               Model model) {

        model.addAttribute("lista", comenzi.listToate());
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        model.addAttribute("titlu", "Toate comenzile din service");
        model.addAttribute("arataDetalii", true);

        return "angajat_comenzi";
    }

    // 3) detalii comanda (si actiuni pe comanda)

    @GetMapping("/comenzi/{id}")
    public String detalii(@PathVariable("id") int comandaId,
                          @RequestParam(required = false) String mesaj,
                          @RequestParam(required = false) String eroare,
                          Model model) {

        // luam comanda dupa id (join mare)
        comanda_angajat_view v = comenzi.findById(comandaId)
                .orElseThrow(() -> new IllegalStateException("Comanda nu exista."));

        // punem comanda in pagina
        model.addAttribute("c", v);

        // punem cataloage pentru dropdown-uri (servicii/piese)
        model.addAttribute("catalogServicii", servicii.findAll());
        model.addAttribute("catalogPiese", piese.findAll());

        // ce servicii sunt deja salvate pe comanda
        model.addAttribute("serviciiExecutate", comenzi.listServiciiExecutate(comandaId));

        // ce piese sunt deja salvate pe comanda (text simplu)
        model.addAttribute("pieseFolosite", comenzi.listPieseFolositeText(comandaId));

        // daca exista deja factura, ascundem butonul de generare
        model.addAttribute("facturaExista", facturi.existaFacturaPentruComanda(comandaId));

        model.addAttribute("mesaj", mesaj);
        model.addAttribute("eroare", eroare);

        return "angajat_comanda_detalii";
    }

    // status: trimisa -> in derulare
    @PostMapping("/comenzi/{id}/start")
    public String startLucru(@PathVariable("id") int comandaId, Authentication auth) {
        try {
            service.pornesteLucrul(auth.getName(), comandaId);
        } catch (Exception ex) {
            return "redirect:/angajat/comenzi/" + comandaId + "?eroare=" + encode(ex.getMessage());
        }
        return "redirect:/angajat/comenzi/" + comandaId + "?mesaj=Status%20setat%20In%20derulare";
    }

    // servicii: salveaza lista de servicii executate (bifate)
    @PostMapping("/comenzi/{id}/servicii")
    public String salveazaServicii(@PathVariable("id") int comandaId,
                                   Authentication auth,
                                   @RequestParam(name = "servicii", required = false) List<Integer> serviciiIds) {
        try {
            service.salveazaServicii(auth.getName(), comandaId, serviciiIds);
        } catch (Exception ex) {
            return "redirect:/angajat/comenzi/" + comandaId + "?eroare=" + encode(ex.getMessage());
        }
        return "redirect:/angajat/comenzi/" + comandaId + "?mesaj=Servicii%20salvate";
    }

    // piese: adauga o piesa + cantitate (scade stoc)
    @PostMapping("/comenzi/{id}/piese")
    public String adaugaPiesa(@PathVariable("id") int comandaId,
                              Authentication auth,
                              @RequestParam int piesaId,
                              @RequestParam int cantitate) {
        try {
            service.adaugaPiesa(auth.getName(), comandaId, piesaId, cantitate);
        } catch (Exception ex) {
            return "redirect:/angajat/comenzi/" + comandaId + "?eroare=" + encode(ex.getMessage());
        }
        return "redirect:/angajat/comenzi/" + comandaId + "?mesaj=Piesa%20a%20fost%20adaugata%20si%20stocul%20scazut";
    }

    // factura: genereaza factura si finalizeaza comanda
    @PostMapping("/comenzi/{id}/factura")
    public String genereazaFactura(@PathVariable("id") int comandaId,
                                   Authentication auth,
                                   @RequestParam String serie,
                                   @RequestParam int numar,
                                   @RequestParam BigDecimal pretFactura) {
        try {
            service.genereazaFactura(auth.getName(), comandaId, serie, numar, pretFactura);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/angajat/comenzi/" + comandaId + "?eroare=" + encode(ex.getMessage());
        }

        return "redirect:/angajat/comenzi/" + comandaId + "?mesaj=Factura%20generata%20si%20comanda%20finalizata";
    }

    // encode simplu pentru redirect (ca sa nu stricam url-ul la mesaj)
    private String encode(String s) {
        if (s == null) return "";
        return s.replace(" ", "%20");
    }

    // get /grafic
    // doar intoarce template-ul (html) pentru grafic
    @GetMapping("/grafic")
    public String paginaGrafic() {
        return "angajat_grafic";
    }
}
