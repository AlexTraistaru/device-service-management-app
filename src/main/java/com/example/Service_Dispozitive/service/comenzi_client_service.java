/** Clasa pentru operatii ale clientului pe comenzile lui.
 * aici sunt functiile pentru:
 * - stergere comanda (doar daca e trimisa)
 * - editare comanda (doar daca e trimisa)
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import com.example.Service_Dispozitive.acces_date.client_acces;
import com.example.Service_Dispozitive.acces_date.comanda_acces;
import com.example.Service_Dispozitive.acces_date.dispozitiv_acces;
import com.example.Service_Dispozitive.entitati.comanda_view;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class comenzi_client_service {

    // acces la clienti (userId -> clientId)
    private final client_acces clienti;

    // acces la comenzi (detalii comanda + delete comanda)
    private final comanda_acces comenzi;

    // acces la dispozitive (update/sterge dispozitiv)
    private final dispozitiv_acces dispozitive;

    public comenzi_client_service(client_acces clienti, comanda_acces comenzi, dispozitiv_acces dispozitive) {
        this.clienti = clienti;
        this.comenzi = comenzi;
        this.dispozitive = dispozitive;
    }

    // sterge comanda si dispozitivul ei, doar daca:
    // - comanda apartine clientului
    // - status este trimisa
    // userId = userul logat
    // comandaId = comanda de sters
    @Transactional
    public void stergeComandaTrimisa(int userId, int comandaId) {

        // 1) aflam clientId din userId
        int clientId = clienti.getClientIdByUserId(userId);

        // 2) luam comanda si verificam ca apartine clientului
        comanda_view v = comenzi.findByClientIdAndComandaId(clientId, comandaId)
                .orElseThrow(() -> new IllegalStateException("Comanda nu exista sau nu iti apartine."));

        // 3) verificam status
        if (!"Trimisa".equalsIgnoreCase(v.getStatus())) {
            throw new IllegalStateException("Comanda nu poate fi stearsa deoarece nu mai este in status Trimisa.");
        }

        // 4) stergem comanda (si legaturile din comanda_servicii / comanda_piese)
        int sters = comenzi.deleteComandaTrimisa(clientId, comandaId);
        if (sters == 0) {
            throw new IllegalStateException("Comanda nu a putut fi stearsa (verifica status/owner).");
        }

        // 5) stergem dispozitivul asociat, ca sa nu ramana in baza de date fara comanda
        dispozitive.deleteDispozitiv(clientId, v.getDispozitivId());
    }

    // editeaza comanda si dispozitivul ei, doar daca:
    // - comanda apartine clientului
    // - status este trimisa
    // userId = userul logat
    // comandaId = comanda editata
    // tipDispozitiv, producator, model, serie = date noi pentru dispozitiv
    // defect = defect nou
    // metodaDePlata = metoda de plata noua
    @Transactional
    public void editeazaComandaTrimisa(int userId,
                                       int comandaId,
                                       String tipDispozitiv,
                                       String producator,
                                       String model,
                                       String serie,
                                       String defect,
                                       String metodaDePlata) {

        // 1) aflam clientId din userId
        int clientId = clienti.getClientIdByUserId(userId);

        // 2) luam comanda si verificam ca apartine clientului
        comanda_view v = comenzi.findByClientIdAndComandaId(clientId, comandaId)
                .orElseThrow(() -> new IllegalStateException("Comanda nu exista sau nu iti apartine."));

        // 3) verificam status
        if (!"Trimisa".equalsIgnoreCase(v.getStatus())) {
            throw new IllegalStateException("Comanda nu poate fi editata deoarece nu mai este in status Trimisa.");
        }

        // 4) update pe dispozitiv (doar pe dispozitivul clientului)
        dispozitive.updateDispozitiv(clientId, v.getDispozitivId(), tipDispozitiv, producator, model, serie);

        // 5) update pe comanda (defect + metoda de plata), doar daca status este trimisa
        dispozitive.updateComandaTrimisa(clientId, comandaId, defect, metodaDePlata);
    }
}
