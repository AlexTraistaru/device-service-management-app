/** Clasa pentru testarea regulilor simple de comanda (fara baza de date).
 * aici testam regula care spune cand o comanda poate fi stearsa.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class reguli_comanda_test {

    // verifica regula: comanda se poate sterge doar cand status este "trimisa", testeaza functia poateStergeComanda
    //creeaza obiectul reguli_comanda
    //apeleaza metoda cu diferite valori de status (stringuri)
    //verifica rezultatul intors (true/false)
    @Test
    void poateStergeComanda_doarCandETrimisa() {
        reguli_comanda r = new reguli_comanda();

        // cazuri care trebuie sa intoarca true
        assertEquals(true, r.poateStergeComanda("Trimisa"));

        // cazuri care trebuie sa intoarca false
        assertEquals(false, r.poateStergeComanda("In derulare"));
        assertEquals(false, r.poateStergeComanda("Finalizata"));
        assertEquals(false, r.poateStergeComanda(null));
    }
}
