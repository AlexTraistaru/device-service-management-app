/** Clasa cu reguli simple pentru comenzi.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

public class reguli_comanda {

    // clientul poate sterge comanda doar daca status este "Trimisa"
    // status = statusul comenzii luat din baza de date
    public boolean poateStergeComanda(String status) {

        // daca status e null, nu permitem stergere
        if (status == null) return false;

        // trim ca sa ignoram spatiile, equalsIgnoreCase ca sa nu conteze literele mari/mici
        return "Trimisa".equalsIgnoreCase(status.trim());
    }
}
