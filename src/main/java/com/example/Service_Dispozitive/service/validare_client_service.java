/** Clasa pentru validari simple ale datelor de client.
 * aici sunt validari folosite la completarea profilului de client.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import org.springframework.stereotype.Service;

@Service
public class validare_client_service {

    // nume obligatoriu: trebuie sa existe si sa nu fie gol dupa trim
    public boolean numeValid(String nume) {
        return nume != null && !nume.trim().isEmpty();
    }

    // prenume obligatoriu: trebuie sa existe si sa nu fie gol dupa trim
    public boolean prenumeValid(String prenume) {
        return prenume != null && !prenume.trim().isEmpty();
    }

    // telefon obligatoriu: trebuie sa existe si sa nu fie gol dupa trim
    public boolean telefonValid(String telefon) {
        return telefon != null && !telefon.trim().isEmpty();
    }

    // email optional:
    // daca nu e completat, e ok
    // daca e completat, trebuie sa respecte un format simplu cu @
    public boolean emailValidOptional(String email) {
        if (email == null || email.isBlank()) return true;
        return email.matches("^[^@\\s]+@[^@\\s]+$");
    }

    // cnp optional:
    // daca nu e completat, e ok
    // daca e completat, trebuie sa fie fix 13 cifre
    public boolean cnpValidOptional(String cnp) {
        if (cnp == null || cnp.isBlank()) return true;
        String x = cnp.trim();
        return x.matches("^\\d{13}$");
    }
}
