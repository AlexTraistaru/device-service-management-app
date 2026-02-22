/** Clasa pentru rute generale: login, inregistrare, redirectionare dupa rol, panou.
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.entitati.utilizator;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import com.example.Service_Dispozitive.acces_date.client_acces;
import com.example.Service_Dispozitive.acces_date.angajat_acces;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
// controller general pentru autentificare si navigare dupa rol
public class controller_web {

    private final utlizator_acces utilizatori;
    private final client_acces clienti;
    private final angajat_acces angajati;
    private final PasswordEncoder encoder;

    public controller_web(utlizator_acces utilizatori, client_acces clienti, angajat_acces angajati, PasswordEncoder encoder) {
        this.utilizatori = utilizatori;
        this.clienti = clienti;
        this.angajati = angajati;
        this.encoder = encoder;
    }

    // validari backend

    // nume utilizator: sa nu fie gol dupa trim
    private boolean numeUtilizatorValid(String numeUtilizator) {
        if (numeUtilizator == null) return false;
        return !numeUtilizator.trim().isEmpty();
    }

    // parola: minim 4 caractere si contine cel putin o cifra
    private boolean parolaValida(String parola) {
        if (parola == null) return false;
        if (parola.length() < 4) return false;
        return parola.matches(".*\\d.*");
    }

    // email optional: daca nu e completat e ok, altfel trebuie sa aiba @
    private boolean emailValidOptional(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return email.matches("^[^@\\s]+@[^@\\s]+$");
    }

    // get /login
    // afiseaza pagina de login (template)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // get /inregistrare
    // afiseaza pagina de signup (template)
    @GetMapping("/inregistrare")
    public String inregistrare() {
        return "inregistrare";
    }

    // post /inregistrare
    // creeaza cont nou in Utilizatori + rand gol in Clienti/Angajati (in functie de rol)
    @PostMapping("/inregistrare")
    public String inregistrarePost(@RequestParam String numeUtilizator,
                                   @RequestParam String parola,
                                   @RequestParam(required = false) String email,
                                   @RequestParam String rol,
                                   Model model) {

        // adunam erori ca sa le afisam toate
        List<String> erori = new ArrayList<>();

        // validari simple
        if (!numeUtilizatorValid(numeUtilizator)) erori.add("Nume utilizator obligatoriu.");
        if (!parolaValida(parola)) erori.add("Parola minim 4 caractere si sa contina o cifra.");
        if (!emailValidOptional(email)) erori.add("Email invalid.");
        if (rol == null || rol.trim().isEmpty()) erori.add("Rol obligatoriu.");

        // daca avem erori, ramanem pe pagina
        if (!erori.isEmpty()) {
            model.addAttribute("erori", erori);
            return "inregistrare";
        }

        // nu permitem username duplicat
        if (utilizatori.findByNumeUtilizator(numeUtilizator.trim()).isPresent()) {
            model.addAttribute("erori", List.of("Exista deja un utilizator cu acest nume."));
            return "inregistrare";
        }

        // cream utilizatorul
        utilizator u = new utilizator();
        u.setNumeUtilizator(numeUtilizator.trim());
        u.setParola(encoder.encode(parola)); // salvam hash
        u.setEmail(email == null || email.trim().isEmpty() ? null : email.trim());
        u.setRol(rol.trim().toUpperCase());

        utilizator salvat = utilizatori.save(u);

        // cream rand gol in tabela specifica rolului
        if ("CLIENT".equalsIgnoreCase(rol)) {
            clienti.creareClientGol(salvat.getId());
        } else if ("ANGAJAT".equalsIgnoreCase(rol)) {
            angajati.creareAngajatGol(salvat.getId());
        }

        // redirect la login cu mesaj ca s-a creat contul
        return "redirect:/login?inregistrat";
    }

    // rute dupa login

    @GetMapping("/")
    public String radacina() {
        return "redirect:/acasa";
    }

    // dupa login, trimitem userul la panoul corect in functie de rol
    @GetMapping("/acasa")
    public String acasa(Authentication autentificare) {

        boolean esteAngajat = autentificare.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ANGAJAT"));

        if (esteAngajat) return "redirect:/angajat/panou";
        return "redirect:/client/panou";
    }

    // panoul angajatului
    @GetMapping("/angajat/panou")
    public String panouAngajat(Model model, Authentication autentificare) {
        model.addAttribute("tipInterfata", "ANGAJAT");
        model.addAttribute("nume", autentificare.getName());
        return "panou";
    }

    // panoul clientului
    @GetMapping("/client/panou")
    public String panouClient(Model model, Authentication autentificare) {
        model.addAttribute("tipInterfata", "CLIENT");
        model.addAttribute("nume", autentificare.getName());
        return "panou";
    }
}
