/** Clasa pentru testarea validarilor de signup (fara baza de date).
 * aici testam ca functia de validare poate intoarce mai multe erori in acelasi timp.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class validare_service_test {

    // verificam ca valideazaSignup poate raporta mai multe probleme dintr-o data
    // aici cream un caz rau:
    // - username gol
    // - parola fara cifra
    // - email cu format gresit
    @Test
    void valideazaSignup_intoarceMaiMulteEroriSimultan() {
        validare_service v = new validare_service();

        // ne asteptam la minim 3 erori
        List<String> erori = v.valideazaSignup("   ", "abcd", "prost_email");

        assertTrue(erori.size() >= 3);

        // verificam ca mesajele contin ideile principale (nu textul exact)
        assertTrue(erori.stream().anyMatch(e -> e.contains("utilizator")));
        assertTrue(erori.stream().anyMatch(e -> e.contains("cifra")));
        assertTrue(erori.stream().anyMatch(e -> e.contains("E-mail")));
    }
}
