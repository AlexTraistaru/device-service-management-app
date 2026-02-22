/** Clasa pentru accesul la comenzi din punctul de vedere al angajatului.
 * aici sunt functiile care citesc comenzile pentru angajati (ale mele / toate),
 * iau detalii despre o comanda, schimba statusul, salveaza servicii executate
 * si salveaza piesele folosite pentru o comanda.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.comanda_angajat_view;
import com.example.Service_Dispozitive.entitati.piesa;
import com.example.Service_Dispozitive.entitati.serviciu;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class comanda_angajat_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public comanda_angajat_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // lista comenzilor asignate acestui angajat
    // angajatId = angajatul logat
    public List<comanda_angajat_view> listAleMele(int angajatId) {

        // bazaSelect() are select-ul mare cu join-uri (comenzi + clienti + dispozitive + departament + angajati)
        // aici punem doar filtrul: doar comenzile care au AngajatID = angajatId
        String sql = bazaSelect() + " WHERE c.AngajatID = ? ORDER BY c.DataPrimire DESC, c.ComandaID DESC";

        // jdbc.query intoarce lista de comanda_angajat_view
        // pentru fiecare rand, apelam map(rs) care construieste obiectul
        return jdbc.query(sql, (rs, n) -> map(rs), angajatId);
    }

    // lista tuturor comenzilor din service
    public List<comanda_angajat_view> listToate() {

        String sql = bazaSelect() + " ORDER BY c.DataPrimire DESC, c.ComandaID DESC";

        // map(rs) transforma randul din sql intr-un obiect comanda_angajat_view
        return jdbc.query(sql, (rs, n) -> map(rs));
    }

    // cauta o comanda dupa comandaId
    // se foloseste pentru pagina de detalii comanda (angajat)
    // comandaId = comanda cautata
    public Optional<comanda_angajat_view> findById(int comandaId) {

        // filtram dupa ComandaID
        String sql = bazaSelect() + " WHERE c.ComandaID = ?";

        // rezultatul vine ca lista, dar ar trebui sa fie maxim un rand
        List<comanda_angajat_view> r = jdbc.query(sql, (rs, n) -> map(rs), comandaId);

        // intoarce primul element daca exista, altfel Optional gol
        return r.stream().findFirst();
    }

    // schimba statusul unei comenzi, dar doar daca angajatul logat este cel asignat pe comanda
    // comandaId = comanda modificata
    // angajatId = angajatul logat
    // statusNou = noul status care se pune in Comenzi.Status
    // returneaza cate randuri au fost modificate (1 daca a mers, 0 daca nu era comanda acelui angajat)
    public int updateStatus(int comandaId, int angajatId, String statusNou) {

        String sql = """
            UPDATE Comenzi
            SET Status = ?
            WHERE ComandaID = ?
              AND AngajatID = ?
        """;

        return jdbc.update(sql, statusNou, comandaId, angajatId);
    }

    // sterge toate serviciile asociate unei comenzi din tabela ComandaServicii
    // se foloseste cand angajatul vrea sa refaca lista de servicii (reset)
    public void deleteServicii(int comandaId) {

        // daca nu exista randuri, delete-ul nu face nimic
        jdbc.update("DELETE FROM ComandaServicii WHERE ComandaID = ?", comandaId);
    }

    // adauga un serviciu executat pentru o comanda
    // dataExecutie se pune automat ca data de azi (GETDATE)
    // comandaId = comanda la care adaugam serviciul
    // serviciuId = serviciul selectat
    public void insertServiciuExecutat(int comandaId, int serviciuId) {

        String sql = """
            INSERT INTO ComandaServicii (ComandaID, ServiciuID, DataExecutie)
            VALUES (?, ?, CAST(GETDATE() AS date))
        """;

        jdbc.update(sql, comandaId, serviciuId);
    }

    // lista serviciilor executate pentru o comanda
    // comandaId = comanda pentru care vrem serviciile
    //Interogarea afiseaza lista de servicii care au fost salvate ca executate pentru o comanda, adica serviciile
    // din tabela de legatura dintre comenzi si servicii. Intoarce denumirea serviciului si pretul standard.
    public List<serviciu> listServiciiExecutate(int comandaId) {

        String sql = """
            SELECT s.ServiciuID, s.Denumire, s.PretStandard
            FROM ComandaServicii cs
            INNER JOIN Servicii s ON s.ServiciuID = cs.ServiciuID
            WHERE cs.ComandaID = ?
            ORDER BY s.Denumire ASC
        """;

        // pentru fiecare rand, construim un obiect serviciu si completam campurile
        return jdbc.query(sql, (rs, n) -> {

            serviciu s = new serviciu();

            // coloane din tabela Servicii
            s.setServiciuId(rs.getInt("ServiciuID"));
            s.setDenumire(rs.getString("Denumire"));
            s.setPretStandard(rs.getBigDecimal("PretStandard"));

            return s;
        }, comandaId);
    }

    // adauga o piesa folosita la o comanda in tabela ComandaPiese
    // daca exista deja piesa pe comanda, cantitatea se aduna peste ce exista
    // comandaId = comanda la care se adauga piesa
    // piesaId = piesa adaugata
    // cantitate = cat se adauga (nu cantitatea finala, ci cat adaugam acum)
    public void upsertComandaPiesa(int comandaId, int piesaId, int cantitate) {

        // daca exista rand in ComandaPiese pentru (comandaId, piesaId) -> update (Cantitate = Cantitate + cantitate)
        // altfel -> insert cu cantitatea primita
        String sql = """
            IF EXISTS (SELECT 1 FROM ComandaPiese WHERE ComandaID = ? AND PiesaID = ?)
                UPDATE ComandaPiese SET Cantitate = Cantitate + ? WHERE ComandaID = ? AND PiesaID = ?;
            ELSE
                INSERT INTO ComandaPiese (ComandaID, PiesaID, Cantitate) VALUES (?, ?, ?);
        """;

        // ordinea parametrilor trebuie sa se potriveasca cu toate semnele ? din sql
        // exists: comandaId, piesaId
        // update: cantitate, comandaId, piesaId
        // insert: comandaId, piesaId, cantitate
        jdbc.update(sql,
                comandaId, piesaId,
                cantitate, comandaId, piesaId,
                comandaId, piesaId, cantitate
        );
    }

    // returneaza piesele folosite pentru o comanda ca text, ca sa fie usor de afisat in pagina
    // comandaId = comanda pentru care vrem piesele
    //Interogarea afiseaza piesele folosite pentru comanda respectiva si cantitatea din fiecare piesa,
    // luand denumirea piesei din Piese si cantitatea din ComandaPiese.
    //Folosesc CAST pentru ca cp.Cantitate este un int iar ca sa concatenez cu text trebuie sa fie si el tot text.
    public List<String> listPieseFolositeText(int comandaId) {

        String sql = """
            SELECT p.Denumire + ' x' + CAST(cp.Cantitate AS varchar(20)) AS Linie
            FROM ComandaPiese cp
            INNER JOIN Piese p ON p.PiesaID = cp.PiesaID
            WHERE cp.ComandaID = ?
            ORDER BY p.Denumire ASC
        """;

        // fiecare rand are o singura coloana "Linie"
        return jdbc.query(sql, (rs, n) -> rs.getString("Linie"), comandaId);
    }

    // intoarce partea comuna de sql folosita in mai multe functii
    // select cu join-uri, ca sa avem intr-un singur rezultat: comanda + client + dispozitiv + departament + angajat
    //Interogarea ia toate comenzile asignate unui anumit angajat si le afiseaza cu detalii complete: date comanda,
    // date client, date dispozitiv, departament si date angajat. Practic e lista “comenzile mele” pentru angajat.
    private String bazaSelect() {
        return """
            SELECT
              c.ComandaID, c.DataPrimire, c.DefectDispozitiv, c.Status, c.MetodaDePlata,
              cl.ClientID, cl.Nume AS ClientNume, cl.Prenume AS ClientPrenume,
              d.DispozitivID, d.TipDispozitiv, d.Producator, d.Model, d.Serie,
              dep.DepartamentID, dep.Denumire AS DepartamentDenumire,
              a.AngajatID, a.Nume AS AngajatNume, a.Prenume AS AngajatPrenume
            FROM Comenzi c
            INNER JOIN Clienti cl ON cl.ClientID = c.ClientID
            INNER JOIN Dispozitive d ON d.DispozitivID = c.DispozitivID
            INNER JOIN Departament dep ON dep.DepartamentID = d.DepartamentID
            INNER JOIN Angajati a ON a.AngajatID = c.AngajatID
        """;
    }

    // transforma un rand din rezultatul sql intr-un obiect comanda_angajat_view
    // rs contine coloanele selectate in bazaSelect()
    private comanda_angajat_view map(java.sql.ResultSet rs) throws java.sql.SQLException {

        comanda_angajat_view v = new comanda_angajat_view();

        // campuri din Comenzi
        v.setComandaId(rs.getInt("ComandaID"));
        v.setDataPrimire(rs.getDate("DataPrimire").toLocalDate());
        v.setDefectDispozitiv(rs.getString("DefectDispozitiv"));
        v.setStatus(rs.getString("Status"));
        v.setMetodaDePlata(rs.getString("MetodaDePlata"));

        // campuri din Clienti
        v.setClientId(rs.getInt("ClientID"));
        v.setClientNume(rs.getString("ClientNume"));
        v.setClientPrenume(rs.getString("ClientPrenume"));

        // campuri din Dispozitive
        v.setDispozitivId(rs.getInt("DispozitivID"));
        v.setTipDispozitiv(rs.getString("TipDispozitiv"));
        v.setProducator(rs.getString("Producator"));
        v.setModel(rs.getString("Model"));
        v.setSerie(rs.getString("Serie"));

        // campuri din Departament
        v.setDepartamentId(rs.getInt("DepartamentID"));
        v.setDepartamentDenumire(rs.getString("DepartamentDenumire"));

        // campuri din Angajati
        v.setAngajatId(rs.getInt("AngajatID"));
        v.setAngajatNume(rs.getString("AngajatNume"));
        v.setAngajatPrenume(rs.getString("AngajatPrenume"));

        return v;
    }

    // lista comenzilor asignate acestui angajat, dar doar din departamentul cerut
    // angajatId = angajatul logat
    // departamentId = departamentul dupa care filtram (departamentul dispozitivului)
    public List<comanda_angajat_view> listAleMeleInDepartament(int angajatId, int departamentId) {

        String sql = bazaSelect()
                + " WHERE c.AngajatID = ? AND d.DepartamentID = ? "
                + " ORDER BY c.DataPrimire DESC, c.ComandaID DESC";

        return jdbc.query(sql, (rs, n) -> map(rs), angajatId, departamentId);
    }

    // lista tuturor comenzilor filtrate dupa status
    // status = statusul dorit (ex: "Trimisa", "In derulare")
    // se foloseste pentru endpoint-ul REST (postman)
    public List<comanda_angajat_view> listToateFiltratStatus(String status) {

        String sql = bazaSelect() + " WHERE c.Status = ? ORDER BY c.DataPrimire DESC, c.ComandaID DESC";

        return jdbc.query(sql, (rs, n) -> map(rs), status);
    }
}
