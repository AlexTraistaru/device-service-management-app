/** Clasa pentru actiunile clientului in interfata web.
 * aici sunt rutele de client:
 *  date personale (afisare + salvare)
 *  creare comanda
 *  comenzile mele
 *  editare comanda (doar daca e trimisa)
 *  stergere comanda (doar daca e trimisa)
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.client_acces;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import com.example.Service_Dispozitive.entitati.client;
import com.example.Service_Dispozitive.entitati.utilizator;
import com.example.Service_Dispozitive.service.validare_client_service;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Service_Dispozitive.acces_date.factura_acces;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.Service_Dispozitive.acces_date.departament_acces;
import com.example.Service_Dispozitive.service.comanda_service;

import com.example.Service_Dispozitive.acces_date.comanda_acces;
import com.example.Service_Dispozitive.entitati.comanda_view;
import com.example.Service_Dispozitive.service.comenzi_client_service;

@Controller
@RequestMapping("/client")
public class client_controller {

    // acces la Utilizatori ca sa luam userId dupa username-ul din login
    private final utlizator_acces utilizatori;

    // acces la Clienti: profil, verificare profil minim, userId -> clientId
    private final client_acces clienti;

    // acces la facturi (lista facturilor clientului)
    private final factura_acces facturi;

    // reguli simple de validare pentru profilul clientului (nume/prenume/telefon etc)
    private final validare_client_service validari;

    // lista departamente pentru dropdown la creare comanda
    private final departament_acces departamente;

    // logica de creare comanda (alege angajat, insert dispozitiv, insert comanda)
    private final comanda_service comandaService;

    // acces pentru lista "comenzile mele"
    private final comanda_acces comenzi;

    // logica pentru editare/stergere comanda (doar daca e trimisa)
    private final comenzi_client_service comenziService;

    // spring injecteaza toate dependintele in constructor
    public client_controller(utlizator_acces utilizatori,
                             client_acces clienti,
                             factura_acces facturi,
                             validare_client_service validari,
                             departament_acces departamente,
                             comanda_service comandaService,
                             comanda_acces comenzi,
                             comenzi_client_service comenziService) {
        this.utilizatori = utilizatori;
        this.clienti = clienti;
        this.facturi = facturi;
        this.validari = validari;
        this.departamente = departamente;
        this.comandaService = comandaService;
        this.comenzi = comenzi;
        this.comenziService = comenziService;
    }

    // pagina cu datele personale ale clientului
    // auth = utilizatorul logat (din spring security)
    // salvat = parametru din url (cand revenim dupa salvare)
    // model = obiectul in care punem date pentru template
    @GetMapping("/date-personale")
    public String paginaDatePersonale(Authentication auth,
                                      @RequestParam(required = false) String salvat,
                                      Model model) {

        // username-ul cu care s-a logat clientul
        String username = auth.getName();

        // cautam utilizatorul in tabela Utilizatori ca sa luam userId
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            // daca nu exista utilizatorul, afisam o eroare si trimitem un client gol ca sa nu crape pagina
            model.addAttribute("erori", List.of("Utilizator inexistent in baza de date."));
            model.addAttribute("client", new client());
            return "date_personale";
        }

        int userId = uOpt.get().getId();

        // luam randul din tabela Clienti dupa userId
        // daca nu exista, construim un client gol, dar cu userId setat
        client c = clienti.findByUserId(userId).orElseGet(() -> {
            client cc = new client();
            cc.setUserId(userId);
            return cc;
        });

        // punem clientul in template ca sa se completeze campurile din form
        model.addAttribute("client", c);

        // daca url-ul este ...?salvat=1, afisam mesaj de succes
        if ("1".equals(salvat)) {
            model.addAttribute("mesajSucces", "Datele au fost salvate.");
        }

        return "date_personale";
    }

    // salveaza datele personale (formularul de profil)
    // toate campurile sunt luate din request, apoi se valideaza si se salveaza in Clienti
    @PostMapping("/date-personale")
    public String salveazaDatePersonale(Authentication auth,
                                        @RequestParam(required = false) String nume,
                                        @RequestParam(required = false) String prenume,
                                        @RequestParam(required = false) String telefon,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String cnp,
                                        @RequestParam(required = false) String strada,
                                        @RequestParam(required = false) String numar,
                                        @RequestParam(required = false) String oras,
                                        @RequestParam(required = false) String judet,
                                        Model model) {

        // username-ul logat
        String username = auth.getName();

        // luam userId din Utilizatori
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            model.addAttribute("erori", List.of("Utilizator inexistent in baza de date."));
            model.addAttribute("client", new client());
            return "date_personale";
        }

        int userId = uOpt.get().getId();

        // curatam inputurile (trim) ca sa nu salvam spatii la inceput/sfarsit
        String numeCurat = (nume == null) ? null : nume.trim();
        String prenumeCurat = (prenume == null) ? null : prenume.trim();
        String telefonCurat = (telefon == null) ? null : telefon.trim();
        String emailCurat = (email == null) ? null : email.trim();
        String cnpCurat = (cnp == null) ? null : cnp.trim();

        String stradaCurat = (strada == null) ? null : strada.trim();
        String numarCurat = (numar == null) ? null : numar.trim();
        String orasCurat = (oras == null) ? null : oras.trim();
        String judetCurat = (judet == null) ? null : judet.trim();

        // aici tinem toate mesajele de eroare, ca sa le afisam pe pagina
        List<String> erori = new ArrayList<>();

        // campuri obligatorii pentru profil minim
        if (!validari.numeValid(numeCurat)) {
            erori.add("Numele este obligatoriu.");
        }
        if (!validari.prenumeValid(prenumeCurat)) {
            erori.add("Prenumele este obligatoriu.");
        }
        if (!validari.telefonValid(telefonCurat)) {
            erori.add("Telefonul este obligatoriu.");
        }

        // campuri optionale: le validam doar daca sunt completate
        if (!validari.emailValidOptional(emailCurat)) {
            erori.add("Email invalid. Exemplu: ana@gmail.com");
        }
        if (!validari.cnpValidOptional(cnpCurat)) {
            erori.add("CNP invalid. Trebuie sa aiba 13 cifre.");
        }

        // daca sunt erori, nu salvam nimic
        // refacem obiectul client cu ce a scris userul ca sa ramana campurile completate
        if (!erori.isEmpty()) {
            model.addAttribute("erori", erori);

            client c = new client();
            c.setUserId(userId);
            c.setNume(numeCurat);
            c.setPrenume(prenumeCurat);
            c.setTelefon(telefonCurat);
            c.setEmail(emailCurat);
            c.setCnp(cnpCurat);
            c.setStrada(stradaCurat);
            c.setNumar(numarCurat);
            c.setOras(orasCurat);
            c.setJudet(judetCurat);

            model.addAttribute("client", c);
            return "date_personale";
        }

        // construim obiectul client complet si il trimitem la accesul de date
        client c = new client();
        c.setUserId(userId);
        c.setNume(numeCurat);
        c.setPrenume(prenumeCurat);
        c.setTelefon(telefonCurat);
        c.setEmail(emailCurat);
        c.setCnp(cnpCurat);
        c.setStrada(stradaCurat);
        c.setNumar(numarCurat);
        c.setOras(orasCurat);
        c.setJudet(judetCurat);

        // update daca exista randul, insert daca nu exista (logica este in client_acces)
        clienti.upsertDatePersonale(c);

        // redirect ca sa nu se retrimita formularul daca userul da refresh
        return "redirect:/client/date-personale?salvat=1";
    }

    // pagina de creare comanda
    // aici se verifica profilul minim, apoi se afiseaza dropdown-ul cu departamente
    @GetMapping("/creeaza-comanda")
    public String paginaCreeazaComanda(Authentication auth, Model model) {

        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            model.addAttribute("erori", List.of("Utilizator inexistent in baza de date."));
            return "creeaza_comanda";
        }

        int userId = uOpt.get().getId();

        // daca nu are nume/prenume/telefon completate, nu are voie sa creeze comanda
        if (!clienti.profilMinimComplet(userId)) {
            return "redirect:/client/date-personale?salvat=0";
        }

        // lista pentru dropdown la departament (categoria dispozitivului)
        model.addAttribute("departamente", departamente.findAll());

        // metoda de plata preselectata in pagina
        model.addAttribute("metodaDefault", "Card");

        return "creeaza_comanda";
    }

    // trimite comanda (post din formular)
    // se mai verifica o data profilul minim, apoi se valideaza campurile comenzii
    @PostMapping("/creeaza-comanda")
    public String trimiteComanda(Authentication auth,
                                 @RequestParam String tipDispozitiv,
                                 @RequestParam int departamentId,
                                 @RequestParam String producator,
                                 @RequestParam String modelDispozitiv,
                                 @RequestParam String serie,
                                 @RequestParam String defectDispozitiv,
                                 @RequestParam String metodaDePlata,
                                 Model model) {

        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            model.addAttribute("erori", List.of("Utilizator inexistent in baza de date."));
            return "creeaza_comanda";
        }

        int userId = uOpt.get().getId();

        // verificare de siguranta: daca intre timp profilul nu mai e complet, il trimitem inapoi
        if (!clienti.profilMinimComplet(userId)) {
            return "redirect:/client/date-personale";
        }

        // lista de erori pentru comanda
        List<String> erori = new ArrayList<>();

        // tip dispozitiv obligatoriu
        if (tipDispozitiv == null || tipDispozitiv.trim().isEmpty()) {
            erori.add("Tip dispozitiv este obligatoriu.");
        }

        // defect obligatoriu
        if (defectDispozitiv == null || defectDispozitiv.trim().isEmpty()) {
            erori.add("Defect dispozitiv este obligatoriu.");
        }

        // metoda de plata obligatorie
        if (metodaDePlata == null || metodaDePlata.trim().isEmpty()) {
            erori.add("Metoda de plata este obligatorie.");
        }

        // daca sunt erori, ramanem pe pagina si repunem dropdown-ul cu departamente
        if (!erori.isEmpty()) {
            model.addAttribute("erori", erori);
            model.addAttribute("departamente", departamente.findAll());
            model.addAttribute("metodaDefault", metodaDePlata);
            return "creeaza_comanda";
        }

        try {
            // aici se creeaza comanda in baza de date
            // comanda_service se ocupa de pasii mari (alege angajat, insert dispozitiv, insert comanda)
            comandaService.creeazaComanda(
                    userId,
                    departamentId,
                    tipDispozitiv.trim(),
                    (producator == null || producator.trim().isEmpty()) ? null : producator.trim(),
                    (modelDispozitiv == null || modelDispozitiv.trim().isEmpty()) ? null : modelDispozitiv.trim(),
                    (serie == null || serie.trim().isEmpty()) ? null : serie.trim(),
                    defectDispozitiv.trim(), // obligatoriu
                    metodaDePlata.trim()
            );
        } catch (Exception ex) {
            // daca apare eroare (ex: nu exista angajati in departamentul ales), afisam mesaj si ramanem in pagina
            model.addAttribute("erori", List.of("Eroare la creare comanda: " + ex.getMessage()));
            model.addAttribute("departamente", departamente.findAll());
            model.addAttribute("metodaDefault", metodaDePlata);
            return "creeaza_comanda";
        }

        // dupa ce comanda s-a creat, trimitem clientul la panou
        return "redirect:/client/panou";
    }

    // lista comenzilor clientului logat
    // mesaj/eroare vin optional din redirect (ex dupa editare/stergere)
    @GetMapping("/comenzile-mele")
    public String comenzileMele(Authentication auth,
                                @RequestParam(required = false) String mesaj,
                                @RequestParam(required = false) String eroare,
                                Model model) {

        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            model.addAttribute("erori", List.of("Utilizator inexistent in baza de date."));
            return "comenzile_mele";
        }

        int userId = uOpt.get().getId();

        // din userId luam clientId (Clienti)
        int clientId = clienti.getClientIdByUserId(userId);

        // lista cu join (Comenzi + Dispozitive + Departament)
        List<comanda_view> lista = comenzi.listByClientId(clientId);
        model.addAttribute("lista", lista);

        // afisam mesajele daca exista
        if (mesaj != null && !mesaj.isBlank()) model.addAttribute("mesajSucces", mesaj);
        if (eroare != null && !eroare.isBlank()) model.addAttribute("eroareText", eroare);

        return "comenzile_mele";
    }

    // get /facturile-mele
    // afiseaza lista de facturi pentru clientul logat
    @GetMapping("/facturile-mele")
    public String facturileMele(Authentication auth, Model model) {

        // luam userul logat
        utilizator u = utilizatori.findByNumeUtilizator(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        // transformam userId -> clientId
        int clientId = clienti.getClientIdByUserId(u.getId());

        // luam facturile clientului (join facturi + comenzi + dispozitive)
        List<Map<String, Object>> lista = facturi.listFacturiByClientId(clientId);

        // punem lista in pagina
        model.addAttribute("lista", lista);

        return "client_facturi";
    }

    // pagina de editare comanda (doar daca e trimisa si apartine clientului)
    @GetMapping("/comenzi/{id}/edit")
    public String paginaEditareComanda(@PathVariable("id") int comandaId,
                                       Authentication auth,
                                       Model model) {

        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            model.addAttribute("erori", List.of("Utilizator inexistent."));
            return "editeaza_comanda";
        }

        int userId = uOpt.get().getId();
        int clientId = clienti.getClientIdByUserId(userId);

        // luam comanda doar daca apartine clientului
        comanda_view v = comenzi.findByClientIdAndComandaId(clientId, comandaId)
                .orElse(null);

        // daca nu exista sau nu e a lui, il trimitem inapoi la lista
        if (v == null) {
            return "redirect:/client/comenzile-mele?eroare=Comanda%20nu%20exista%20sau%20nu%20iti%20apartine";
        }

        // editare permisa doar cand status este trimisa
        if (!v.esteEditabila()) {
            return "redirect:/client/comenzile-mele?eroare=Comanda%20nu%20poate%20fi%20editata%20deoarece%20nu%20mai%20este%20in%20status%20Trimisa";
        }

        // punem comanda in model ca sa se completeze form-ul
        model.addAttribute("c", v);
        return "editeaza_comanda";
    }

    // salveaza editarea comenzii (doar daca e trimisa)
    @PostMapping("/comenzi/{id}/edit")
    public String salveazaEditareComanda(@PathVariable("id") int comandaId,
                                         Authentication auth,
                                         @RequestParam String tipDispozitiv,
                                         @RequestParam(required = false) String producator,
                                         @RequestParam(required = false) String modelDispozitiv,
                                         @RequestParam(required = false) String serie,
                                         @RequestParam String defectDispozitiv,
                                         @RequestParam String metodaDePlata,
                                         Model model) {

        // validari simple pentru campurile obligatorii
        List<String> erori = new ArrayList<>();

        if (tipDispozitiv == null || tipDispozitiv.trim().isEmpty()) erori.add("Tip dispozitiv este obligatoriu.");
        if (defectDispozitiv == null || defectDispozitiv.trim().isEmpty()) erori.add("Defect dispozitiv este obligatoriu.");
        if (metodaDePlata == null || metodaDePlata.trim().isEmpty()) erori.add("Metoda de plata este obligatorie.");

        // daca sunt erori, ramanem pe pagina de editare si refacem obiectul comanda ca sa ramana campurile completate
        if (!erori.isEmpty()) {
            model.addAttribute("erori", erori);

            comanda_view v = new comanda_view();
            v.setComandaId(comandaId);
            v.setTipDispozitiv(tipDispozitiv);
            v.setProducator(producator);
            v.setModel(modelDispozitiv);
            v.setSerie(serie);
            v.setDefectDispozitiv(defectDispozitiv);
            v.setMetodaDePlata(metodaDePlata);
            v.setStatus("Trimisa"); // ca pagina de editare este doar pentru trimisa
            model.addAttribute("c", v);

            return "editeaza_comanda";
        }

        // luam userId din username
        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            return "redirect:/client/comenzile-mele?eroare=Utilizator%20inexistent";
        }

        int userId = uOpt.get().getId();

        try {
            // service-ul verifica si face update la dispozitiv + comanda
            comenziService.editeazaComandaTrimisa(
                    userId,
                    comandaId,
                    tipDispozitiv.trim(),
                    (producator == null || producator.trim().isEmpty()) ? null : producator.trim(),
                    (modelDispozitiv == null || modelDispozitiv.trim().isEmpty()) ? null : modelDispozitiv.trim(),
                    (serie == null || serie.trim().isEmpty()) ? null : serie.trim(),
                    defectDispozitiv.trim(),
                    metodaDePlata.trim()
            );
        } catch (Exception ex) {
            // redirect cu mesaj de eroare in url (spatiile sunt inlocuite cu %20)
            return "redirect:/client/comenzile-mele?eroare=Eroare%20la%20editare:%20" + ex.getMessage().replace(" ", "%20");
        }

        return "redirect:/client/comenzile-mele?mesaj=Comanda%20a%20fost%20actualizata";
    }

    // sterge comanda (doar daca e trimisa)
    @PostMapping("/comenzi/{id}/sterge")
    public String stergeComanda(@PathVariable("id") int comandaId,
                                Authentication auth) {

        // luam userId din username
        String username = auth.getName();
        Optional<utilizator> uOpt = utilizatori.findByNumeUtilizator(username);
        if (uOpt.isEmpty()) {
            return "redirect:/client/comenzile-mele?eroare=Utilizator%20inexistent";
        }

        int userId = uOpt.get().getId();

        try {
            // service-ul sterge comanda doar daca e a clientului si status este trimisa
            comenziService.stergeComandaTrimisa(userId, comandaId);
        } catch (Exception ex) {
            // redirect cu mesaj de eroare in url
            return "redirect:/client/comenzile-mele?eroare=Eroare%20la%20stergere:%20" + ex.getMessage().replace(" ", "%20");
        }

        return "redirect:/client/comenzile-mele?mesaj=Comanda%20a%20fost%20stearsa";
    }
}
