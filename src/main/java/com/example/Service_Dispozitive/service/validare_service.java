/** Clasa pentru validari simple la signup.
 * functiile intorc lista de erori, ca sa putem afisa mai multe erori odata.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import java.util.ArrayList;
import java.util.List;

public class validare_service {

    // valideaza username-ul de la signup
    // username = numele de utilizator introdus
    // intoarce lista de erori (goala daca e ok)
    public List<String> valideazaUsername(String username) {

        List<String> erori = new ArrayList<>();

        // username obligatoriu
        if (username == null || username.trim().isEmpty()) {
            erori.add("Numele de utilizator nu poate fi gol.");
        }

        return erori;
    }

    // valideaza parola de la signup
    // parola = parola introdusa
    // regula: minim 4 caractere si cel putin o cifra
    public List<String> valideazaParola(String parola) {

        List<String> erori = new ArrayList<>();

        // parola obligatorie si minim 4 caractere
        if (parola == null || parola.length() < 4) {
            erori.add("Parola trebuie sa aiba minim 4 caractere.");
            return erori;
        }

        // verifica daca exista macar o cifra in parola
        if (!parola.matches(".*\\d.*")) {
            erori.add("Parola trebuie sa contina cel putin o cifra.");
        }

        return erori;
    }

    // valideaza email optional
    // email = email introdus
    // daca nu e completat, nu adaugam erori
    // daca e completat, verificam format simplu
    public List<String> valideazaEmailOptional(String email) {

        List<String> erori = new ArrayList<>();

        // email optional
        if (email == null || email.isBlank()) return erori;

        String e = email.trim();

        if (!e.matches("^[^@\\s]+@[^@\\s]+$")) {
            erori.add("E-mail invalid. Exemplu corect: ana@gmail.com");
        }

        return erori;
    }

    // valideaza toate campurile de signup si aduna toate erorile
    // username = nume utilizator
    // parola = parola
    // email = email optional
    public List<String> valideazaSignup(String username, String parola, String email) {

        List<String> erori = new ArrayList<>();

        // adunam erorile din fiecare validare
        erori.addAll(valideazaUsername(username));
        erori.addAll(valideazaParola(parola));
        erori.addAll(valideazaEmailOptional(email));

        return erori;
    }
}
