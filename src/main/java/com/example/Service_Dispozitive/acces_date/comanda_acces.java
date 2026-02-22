/** Clasa pentru accesul la tabela Comenzi din baza de date.
 * aici sunt functiile care creeaza o comanda, lista comenzile unui client,
 * ia detaliile unei comenzi pentru editare si sterge o comanda trimisa.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.comanda_view;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository // spune lui spring ca aceasta clasa se ocupa cu accesul la baza de date
public class comanda_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public comanda_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // insereaza o comanda in tabela Comenzi
    // clientId = clientul care a facut comanda
    // dispozitivId = dispozitivul adus in service
    // angajatId = angajatul care primeste comanda (poate fi ales automat in service)
    // dataPrimire = data la care clientul a adus dispozitivul
    // defectDispozitiv = descrierea problemei (scrisa de client)
    // status = status initial al comenzii ("Trimisa")
    // metodaDePlata = cash / card
    public void insertComanda(int clientId,
                              int dispozitivId,
                              int angajatId,
                              LocalDate dataPrimire,
                              String defectDispozitiv,
                              String status,
                              String metodaDePlata) {

        String sql = """
            INSERT INTO Comenzi (ClientID, DispozitivID, AngajatID, DataPrimire, DefectDispozitiv, Status, MetodaDePlata)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        // jdbc.update executa insert-ul si trimite parametrii pe rand
        jdbc.update(sql,
                clientId,
                dispozitivId,
                angajatId,
                dataPrimire,
                defectDispozitiv,
                status,
                metodaDePlata
        );
    }

    // returneaza lista cu comenzile unui client, pentru pagina "comenzile mele"
    // face join intre Comenzi, Dispozitive si Departament ca sa afisam si datele dispozitivului si departamentul
    // clientId = clientul logat
    //Interogarea afiseaza toate comenzile unui client, impreuna cu datele dispozitivului din comanda si denumirea
    // departamentului dispozitivului. Rezultatul este sortat descrescator dupa data primirii si id-ul comenzii
    public List<comanda_view> listByClientId(int clientId) {

        String sql = """
            SELECT
                c.ComandaID,
                c.DataPrimire,
                c.DefectDispozitiv,
                c.Status,
                c.MetodaDePlata,
                d.DispozitivID,
                d.TipDispozitiv,
                d.Producator,
                d.Model,
                d.Serie,
                d.DepartamentID,
                dep.Denumire AS DepartamentDenumire
            FROM Comenzi c
            INNER JOIN Dispozitive d ON d.DispozitivID = c.DispozitivID
            INNER JOIN Departament dep ON dep.DepartamentID = d.DepartamentID
            WHERE c.ClientID = ?
            ORDER BY c.DataPrimire DESC, c.ComandaID DESC
        """;

        // jdbc.query intoarce o lista de comanda_view
        // pentru fiecare rand din rezultat, construim un obiect comanda_view si ii completam campurile
        return jdbc.query(sql, (rs, rowNum) -> {

            comanda_view v = new comanda_view();

            // campuri din tabela Comenzi
            v.setComandaId(rs.getInt("ComandaID"));
            v.setDataPrimire(rs.getDate("DataPrimire").toLocalDate());
            v.setDefectDispozitiv(rs.getString("DefectDispozitiv"));
            v.setStatus(rs.getString("Status"));
            v.setMetodaDePlata(rs.getString("MetodaDePlata"));

            // campuri din tabela Dispozitive
            v.setDispozitivId(rs.getInt("DispozitivID"));
            v.setTipDispozitiv(rs.getString("TipDispozitiv"));
            v.setProducator(rs.getString("Producator"));
            v.setModel(rs.getString("Model"));
            v.setSerie(rs.getString("Serie"));

            // campuri despre departament (id din Dispozitive, denumire din Departament)
            v.setDepartamentId(rs.getInt("DepartamentID"));
            v.setDepartamentDenumire(rs.getString("DepartamentDenumire"));

            return v;
        }, clientId);
    }

    // cauta o comanda a unui client dupa id-ul comenzii
    // se foloseste cand clientul vrea sa editeze o comanda si trebuie sa incarcam datele ei
    // clientId = clientul logat (asa ne asiguram ca nu vede comenzi care nu sunt ale lui)
    // comandaId = comanda selectata
    //Interogarea cauta o singura comanda, dar doar daca apartine clientului logat. Intoarce detaliile comenzii
    // impreuna cu datele dispozitivului si departamentul dispozitivului. Este folosita cand clientul
    // vrea sa editeze comanda.
    public Optional<comanda_view> findByClientIdAndComandaId(int clientId, int comandaId) {

        String sql = """
            SELECT
                c.ComandaID,
                c.DataPrimire,
                c.DefectDispozitiv,
                c.Status,
                c.MetodaDePlata,
                d.DispozitivID,
                d.TipDispozitiv,
                d.Producator,
                d.Model,
                d.Serie,
                d.DepartamentID,
                dep.Denumire AS DepartamentDenumire
            FROM Comenzi c
            INNER JOIN Dispozitive d ON d.DispozitivID = c.DispozitivID
            INNER JOIN Departament dep ON dep.DepartamentID = d.DepartamentID
            WHERE c.ClientID = ?
              AND c.ComandaID = ?
        """;

        // rezultatul vine ca lista, chiar daca ar trebui sa fie maxim un rand
        List<comanda_view> rezultat = jdbc.query(sql, (rs, rowNum) -> {

            comanda_view v = new comanda_view();

            // campuri din Comenzi
            v.setComandaId(rs.getInt("ComandaID"));
            v.setDataPrimire(rs.getDate("DataPrimire").toLocalDate());
            v.setDefectDispozitiv(rs.getString("DefectDispozitiv"));
            v.setStatus(rs.getString("Status"));
            v.setMetodaDePlata(rs.getString("MetodaDePlata"));

            // campuri din Dispozitive
            v.setDispozitivId(rs.getInt("DispozitivID"));
            v.setTipDispozitiv(rs.getString("TipDispozitiv"));
            v.setProducator(rs.getString("Producator"));
            v.setModel(rs.getString("Model"));
            v.setSerie(rs.getString("Serie"));

            // departament
            v.setDepartamentId(rs.getInt("DepartamentID"));
            v.setDepartamentDenumire(rs.getString("DepartamentDenumire"));

            return v;
        }, clientId, comandaId);

        // intoarce primul element daca exista, altfel Optional gol
        return rezultat.stream().findFirst();
    }

    // sterge o comanda doar daca este a clientului si are status "Trimisa"
    // inainte sa stergem comanda, stergem legaturile din ComandaServicii si ComandaPiese ca sa nu pice cheia straina
    // clientId = clientul logat
    // comandaId = comanda de sters
    // returneaza cate randuri au fost sterse din Comenzi (1 daca s-a sters, 0 daca nu avea voie)
    public int deleteComandaTrimisa(int clientId, int comandaId) {

        // sterge serviciile asociate comenzii (daca exista)
        // daca nu exista randuri, delete-ul nu face nimic
        jdbc.update("DELETE FROM ComandaServicii WHERE ComandaID = ?", comandaId);

        // sterge piesele asociate comenzii (daca exista)
        jdbc.update("DELETE FROM ComandaPiese WHERE ComandaID = ?", comandaId);

        // sterge comanda doar daca:
        // comandaId este cea ceruta
        // comanda apartine clientului (ClientID = clientId)
        // status este "Trimisa"
        String sql = """
            DELETE FROM Comenzi
            WHERE ComandaID = ?
              AND ClientID = ?
              AND Status = N'Trimisa'
        """;

        return jdbc.update(sql, comandaId, clientId);
    }
}
