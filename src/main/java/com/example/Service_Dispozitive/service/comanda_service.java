/** Clasa pentru crearea unei comenzi de catre client.
 * aici se face pasul complet de creare:
 * - se afla clientId din userId
 * - se alege un angajat disponibil din departamentul ales
 * - se insereaza dispozitivul si se obtine DispozitivID
 * - se insereaza comanda cu status trimisa
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import com.example.Service_Dispozitive.acces_date.angajat_acces;
import com.example.Service_Dispozitive.acces_date.client_acces;
import com.example.Service_Dispozitive.acces_date.comanda_acces;
import com.example.Service_Dispozitive.acces_date.dispozitiv_acces;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class comanda_service {

    // acces la clienti (userId -> clientId)
    private final client_acces clienti;

    // acces la angajati (alegere angajat disponibil)
    private final angajat_acces angajati;

    // acces la dispozitive (insert dispozitiv)
    private final dispozitiv_acces dispozitive;

    // acces la comenzi (insert comanda)
    private final comanda_acces comenzi;

    public comanda_service(client_acces clienti,
                           angajat_acces angajati,
                           dispozitiv_acces dispozitive,
                           comanda_acces comenzi) {
        this.clienti = clienti;
        this.angajati = angajati;
        this.dispozitive = dispozitive;
        this.comenzi = comenzi;
    }

    // creeaza comanda impreuna cu dispozitivul
    // este tranzactie: daca pica una din operatii, nu ramane nimic salvat partial
    // userId = userul logat (din Utilizatori)
    // departamentId = departamentul ales in formular
    // tipDispozitiv, producator, model, serie = datele dispozitivului
    // defectDispozitiv = descriere defect
    // metodaDePlata = card/numerar
    @Transactional
    public void creeazaComanda(int userId,
                               int departamentId,
                               String tipDispozitiv,
                               String producator,
                               String model,
                               String serie,
                               String defectDispozitiv,
                               String metodaDePlata) {

        // 1) din userId aflam clientId
        int clientId = clienti.getClientIdByUserId(userId);

        // 2) alegem angajatul cel mai disponibil din departamentul selectat
        Integer angajatId = angajati.alegeAngajatCelMaiDisponibil(departamentId);
        if (angajatId == null) {
            throw new IllegalStateException("Nu exista angajati in departamentul selectat.");
        }

        // 3) inseram dispozitivul si luam DispozitivID-ul generat
        int dispozitivId = dispozitive.insertDispozitiv(
                clientId,
                departamentId,
                tipDispozitiv,
                producator,
                model,
                serie
        );

        // 4) inseram comanda legata de client, dispozitiv si angajat
        comenzi.insertComanda(
                clientId,
                dispozitivId,
                angajatId,
                LocalDate.now(),         // DataPrimire = azi
                defectDispozitiv,
                "Trimisa",               // status initial
                metodaDePlata            // card/numerar
        );
    }
}
