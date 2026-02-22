/** Clasa pentru logica de lucru a angajatului pe comenzi.
 * aici sunt functiile pe care le foloseste angajatul:
 *  porneste lucrul la o comanda (trimisa - in derulare)
 *  salveaza lista de servicii executate
 *  adauga piese (scade stoc + salveaza in comanda)
 *  genereaza factura si finalizeaza comanda
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import com.example.Service_Dispozitive.acces_date.angajat_acces;
import com.example.Service_Dispozitive.acces_date.comanda_angajat_acces;
import com.example.Service_Dispozitive.acces_date.factura_acces;
import com.example.Service_Dispozitive.acces_date.piesa_acces;
import com.example.Service_Dispozitive.acces_date.serviciu_acces;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import com.example.Service_Dispozitive.entitati.comanda_angajat_view;
import com.example.Service_Dispozitive.entitati.utilizator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class angajat_comanda_service {

    // acces la tabela Utilizatori (pentru a converti username -> userId)
    private final utlizator_acces utilizatori;

    // acces la tabela Angajati (pentru a converti userId -> angajatId)
    private final angajat_acces angajati;

    // acces la comenzi pentru partea de angajat (selecturi + update status + legaturi servicii/piese)
    private final comanda_angajat_acces comenzi;

    // acces la serviciile din catalog
    private final serviciu_acces servicii;

    // acces la piese (stoc)
    private final piesa_acces piese;

    // acces la facturi
    private final factura_acces facturi;

    public angajat_comanda_service(utlizator_acces utilizatori,
                                   angajat_acces angajati,
                                   comanda_angajat_acces comenzi,
                                   serviciu_acces servicii,
                                   piesa_acces piese,
                                   factura_acces facturi) {
        this.utilizatori = utilizatori;
        this.angajati = angajati;
        this.comenzi = comenzi;
        this.servicii = servicii;
        this.piese = piese;
        this.facturi = facturi;
    }

    // ia angajatId pornind de la username-ul din autentificare
    // username = numele de utilizator cu care s-a logat angajatul
    private int angajatIdDinAuth(String username) {

        // cautam utilizatorul in tabela Utilizatori
        utilizator u = utilizatori.findByNumeUtilizator(username)
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        // din userId luam angajatId din tabela Angajati
        Integer angajatId = angajati.getAngajatIdByUserId(u.getId());

        // daca nu exista rand in Angajati, inseamna ca utilizatorul nu are profil de angajat
        if (angajatId == null) throw new IllegalStateException("Angajat inexistent in tabela Angajati.");

        return angajatId;
    }

    // ia comanda si verifica daca este asignata angajatului curent
    // comandaId = comanda pe care vrea sa o modifice angajatul
    // angajatId = angajatul logat
    private comanda_angajat_view comandaMeaSauEroare(int comandaId, int angajatId) {

        // cautam comanda in baza de date
        comanda_angajat_view c = comenzi.findById(comandaId)
                .orElseThrow(() -> new IllegalStateException("Comanda nu exista."));

        // verificam ca angajatul de pe comanda este acelasi cu cel logat
        // daca nu, oprim ca sa nu poata modifica comenzi ale altcuiva
        if (c.getAngajatId() != angajatId) {
            throw new IllegalStateException("Nu ai voie sa modifici o comanda care nu iti este asignata.");
        }

        return c;
    }

    // schimba statusul din trimisa in in derulare
    // username = angajatul logat
    // comandaId = comanda pentru care pornim lucrul
    @Transactional
    public void pornesteLucrul(String username, int comandaId) {

        int angajatId = angajatIdDinAuth(username);

        // ia comanda si verifica owner-ul
        comanda_angajat_view c = comandaMeaSauEroare(comandaId, angajatId);

        // poate porni lucrul doar daca status e trimisa
        if (!c.esteTrimisa()) {
            throw new IllegalStateException("Poti porni lucrul doar daca statusul este Trimisa.");
        }

        // update-ul are si conditia angajatId, deci e safe
        int rows = comenzi.updateStatus(comandaId, angajatId, "In derulare");

        // daca rows = 0 inseamna ca update-ul nu s-a facut (de obicei din cauza conditionarii)
        if (rows == 0) throw new IllegalStateException("Nu s-a putut schimba statusul.");
    }

    // salveaza lista de servicii executate la o comanda
    // username = angajatul logat
    // comandaId = comanda modificata
    // serviciuIds = lista cu id-urile serviciilor bifate
    @Transactional
    public void salveazaServicii(String username, int comandaId, List<Integer> serviciuIds) {

        int angajatId = angajatIdDinAuth(username);

        // ia comanda si verifica owner-ul
        comanda_angajat_view c = comandaMeaSauEroare(comandaId, angajatId);

        // la finalizata nu mai permitem modificari
        if (c.esteFinalizata()) {
            throw new IllegalStateException("Nu poti modifica serviciile la o comanda Finalizata.");
        }

        // obligatoriu cel putin un serviciu
        if (serviciuIds == null || serviciuIds.isEmpty()) {
            throw new IllegalStateException("Trebuie sa selectezi cel putin un serviciu.");
        }

        // stergem tot ce era inainte
        comenzi.deleteServicii(comandaId);

        // inseram fiecare serviciu selectat cu dataExecutie = azi
        for (Integer sid : serviciuIds) {
            comenzi.insertServiciuExecutat(comandaId, sid);
        }
    }

    // adauga o piesa folosita pe comanda
    // username = angajatul logat
    // comandaId = comanda modificata
    // piesaId = piesa aleasa din stoc
    // cantitate = cate bucati se folosesc
    @Transactional
    public void adaugaPiesa(String username, int comandaId, int piesaId, int cantitate) {

        int angajatId = angajatIdDinAuth(username);

        // ia comanda si verifica owner-ul
        comanda_angajat_view c = comandaMeaSauEroare(comandaId, angajatId);

        // la finalizata nu mai permitem modificari
        if (c.esteFinalizata()) {
            throw new IllegalStateException("Nu poti adauga piese la o comanda Finalizata.");
        }

        // cantitatea trebuie sa fie macar 1
        if (cantitate <= 0) {
            throw new IllegalStateException("Cantitatea trebuie sa fie pozitiva.");
        }

        // 1) scadem stocul
        // daca nu exista suficient stoc, update-ul nu face nimic si rows = 0
        int rows = piese.scadeStoc(piesaId, cantitate);
        if (rows == 0) {
            throw new IllegalStateException("Stoc insuficient pentru piesa selectata.");
        }

        // 2) salvam piesa pe comanda in ComandaPiese
        // daca exista deja, aduna cantitatea peste ce era
        comenzi.upsertComandaPiesa(comandaId, piesaId, cantitate);
    }

    // genereaza factura pentru comanda si seteaza comanda ca finalizata
    // username = angajatul logat
    // comandaId = comanda finalizata
    // serie = seria facturii
    // numar = numarul facturii
    // pretFactura = pretul total calculat
    @Transactional
    public void genereazaFactura(String username, int comandaId, String serie, int numar, BigDecimal pretFactura) {

        int angajatId = angajatIdDinAuth(username);

        // ia comanda si verifica owner-ul
        comanda_angajat_view c = comandaMeaSauEroare(comandaId, angajatId);

        // daca e deja finalizata, nu mai facem nimic
        if (c.esteFinalizata()) {
            throw new IllegalStateException("Comanda este deja Finalizata.");
        }

        // nu permitem a doua factura pe aceeasi comanda
        if (facturi.existaFacturaPentruComanda(comandaId)) {
            throw new IllegalStateException("Factura exista deja pentru aceasta comanda.");
        }

        // validari simple pentru campurile facturii
        if (serie == null || serie.trim().isEmpty()) throw new IllegalStateException("Serie factura obligatorie.");
        if (numar <= 0) throw new IllegalStateException("Numar factura invalid.");
        if (pretFactura == null || pretFactura.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Pret factura invalid.");
        }

        // insereaza factura, metodaDePlata se ia din Comenzi
        facturi.insertFacturaDinComanda(comandaId, serie.trim(), numar, LocalDate.now(), pretFactura);

        // dupa emiterea facturii, comanda devine finalizata
        comenzi.updateStatus(comandaId, angajatId, "Finalizata");
    }
}
