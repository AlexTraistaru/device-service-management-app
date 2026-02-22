/** Clasa pentru accesul la rapoarte (interogari complexe) din baza de date.
 * aici sunt functiile pentru rapoarte care folosesc subcereri.
 * fiecare raport intoarce o lista de obiecte speciale
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.raport_angajat_disponibil;
import com.example.Service_Dispozitive.entitati.raport_comanda_fara_factura;
import com.example.Service_Dispozitive.entitati.raport_piesa_reaprovizionare;
import com.example.Service_Dispozitive.entitati.raport_top_client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class raport_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public raport_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // raport: angajatul cel mai disponibil dintr-un departament
    // departamentId = departamentul in care cautam
    // returneaza angajatii care au cele mai putine comenzi active (poate fi mai mult de unul daca sunt la egalitate)
    // comenzi active inseamna status "Trimisa" sau "In derulare"
    //Interogarea gaseste angajatul (sau angajatii) dintr-un departament care are cele mai putine comenzi active, adica in status “Trimisa” sau “In derulare”. Foloseste subcereri ca sa numere comenzile active pentru fiecare angajat si ca sa compare cu minimul din departament.
    public List<raport_angajat_disponibil> angajatCelMaiDisponibil(int departamentId) {

        String sql = """
            SELECT
              a.Nume,
              a.Prenume,
              d.Denumire AS Departament,
              (SELECT COUNT(*)
               FROM Comenzi c
               WHERE c.AngajatID = a.AngajatID
                 AND c.Status IN (N'Trimisa', N'In derulare')) AS ComenziActive
            FROM Angajati a
            INNER JOIN Departament d ON d.DepartamentID = a.DepartamentID
            WHERE a.DepartamentID = ?
              AND (SELECT COUNT(*)
                   FROM Comenzi c
                   WHERE c.AngajatID = a.AngajatID
                     AND c.Status IN (N'Trimisa', N'In derulare'))
                  =
                  /*“comenzile active ale angajatului curent” = “minimul comenzilor active din departament”*/
                  /*t.cnt retine numarul de comenzi al fiecarui angajat in parte*/
                  (SELECT MIN(t.cnt)
                   FROM (
                        SELECT
                          (SELECT COUNT(*)
                           FROM Comenzi c2
                           WHERE c2.AngajatID = a2.AngajatID
                             AND c2.Status IN (N'Trimisa', N'In derulare')) AS cnt
                        FROM Angajati a2
                        WHERE a2.DepartamentID = ?
                   ) t)
            ORDER BY a.Nume ASC, a.Prenume ASC
        """;

        // sunt doua semne ? in sql, ambele primesc departamentId
        // 1) filtreaza angajatii doar din departamentul cerut
        // 2) calculeaza minimul tot doar in acel departament
        return jdbc.query(sql, (rs, n) -> {

            raport_angajat_disponibil r = new raport_angajat_disponibil();

            r.setNume(rs.getString("Nume"));
            r.setPrenume(rs.getString("Prenume"));
            r.setDepartament(rs.getString("Departament"));
            r.setComenziActive(rs.getInt("ComenziActive"));

            return r;
        }, departamentId, departamentId);
    }

    // raport: comenzi fara factura pana la o anumita data
    // dataLimita = pana la ce data luam comenzile (inclusiv)
    // intoarce comenzile care nu au factura in tabela Facturi
    //Interogarea afiseaza comenzile primite pana la o data limita care nu au factura. Verifica lipsa
    // facturii folosind NOT EXISTS pe tabela Facturi.
    public List<raport_comanda_fara_factura> comenziFaraFacturaPanaLa(LocalDate dataLimita) {

        String sql = """
            SELECT
              c.DataPrimire,
              ISNULL(cl.Nume, N'') + N' ' + ISNULL(cl.Prenume, N'') AS Client,
              dep.Denumire AS Departament,
              d.TipDispozitiv,
              c.Status
            FROM Comenzi c
            INNER JOIN Clienti cl ON cl.ClientID = c.ClientID
            INNER JOIN Dispozitive d ON d.DispozitivID = c.DispozitivID
            INNER JOIN Departament dep ON dep.DepartamentID = d.DepartamentID
            WHERE c.DataPrimire <= ?
              AND NOT EXISTS (
                    SELECT 1
                    FROM Facturi f
                    WHERE f.ComandaID = c.ComandaID
              )
            ORDER BY c.DataPrimire ASC
        """;

        // trimitem dataLimita ca java.sql.Date ca sa fie compatibil cu JDBC
        return jdbc.query(sql, (rs, n) -> {

            raport_comanda_fara_factura r = new raport_comanda_fara_factura();

            r.setDataPrimire(rs.getDate("DataPrimire").toLocalDate());

            // in sql numele poate fi null, de aceea se foloseste coalesce si apoi trim
            r.setClient(rs.getString("Client").trim());

            r.setDepartament(rs.getString("Departament"));
            r.setTipDispozitiv(rs.getString("TipDispozitiv"));
            r.setStatus(rs.getString("Status"));

            return r;
        }, java.sql.Date.valueOf(dataLimita));
    }

    // raport: top clienti dupa totalul facturat intr-un interval de date
    // dataStart = inceput interval
    // dataEnd = sfarsit interval
    // pragMinim = total minim ca sa apara in lista
    //Interogarea afiseaza clientii care au totalul facturat peste un prag minim intr-un interval de timp.
    // Pentru fiecare client calculeaza numarul de facturi si suma totala folosind subcereri cu COUNT si SUM, apoi
    // filtreaza clientii care trec pragul si ii ordoneaza descrescator dupa total.
    public List<raport_top_client> topClienti(LocalDate dataStart, LocalDate dataEnd, BigDecimal pragMinim) {

        String sql = """
            SELECT
              cl.Nume,
              cl.Prenume,
              ISNULL((
                  SELECT COUNT(*)
                  FROM Facturi f
                  INNER JOIN Comenzi c ON c.ComandaID = f.ComandaID
                  WHERE c.ClientID = cl.ClientID
                    AND f.DataEmitere BETWEEN ? AND ?
              ), 0) AS NrFacturi,
              ISNULL((
                  SELECT SUM(f.PretFactura)
                  FROM Facturi f
                  INNER JOIN Comenzi c ON c.ComandaID = f.ComandaID
                  WHERE c.ClientID = cl.ClientID
                    AND f.DataEmitere BETWEEN ? AND ?
              ), 0) AS Total
            FROM Clienti cl
            WHERE ISNULL((
                  SELECT SUM(f.PretFactura)
                  FROM Facturi f
                  INNER JOIN Comenzi c ON c.ComandaID = f.ComandaID
                  WHERE c.ClientID = cl.ClientID
                    AND f.DataEmitere BETWEEN ? AND ?
            ), 0) >= ?
            ORDER BY Total DESC, NrFacturi DESC, cl.Nume ASC, cl.Prenume ASC
        """;

        // convertim localDate in java.sql.Date pentru jdbc
        java.sql.Date ds = java.sql.Date.valueOf(dataStart);
        java.sql.Date de = java.sql.Date.valueOf(dataEnd);

        // ordinea parametrilor trebuie sa se potriveasca cu semnele ? din sql:
        // 1-2 pentru nrFacturi
        // 3-4 pentru total
        // 5-6 pentru filtrul din where
        // 7 pentru pragMinim
        return jdbc.query(sql, (rs, n) -> {

            raport_top_client r = new raport_top_client();

            r.setNume(rs.getString("Nume"));
            r.setPrenume(rs.getString("Prenume"));
            r.setNrFacturi(rs.getInt("NrFacturi"));
            r.setTotal(rs.getBigDecimal("Total"));

            return r;
        }, ds, de, ds, de, ds, de, pragMinim);
    }

    // raport: piese care au stoc sub un prag si au fost folosite in ultimele N zile
    // pragStoc = limita maxima pentru stoc
    // zile = cate zile in urma verificam consumul
    // intoarce piesele cu stoc mic, impreuna cu suma cantitatilor folosite in ultimele zile
    //Interogarea afiseaza piesele care au stoc sub sau egal cu un prag si calculeaza cat s-a consumat din ele
    // in ultimele N zile. Consum inseamna suma cantitatilor din ComandaPiese pentru comenzile din perioada respectiva,
    // calculata printr-o subcerere.
    //Am folosit CAST, pentru a include complet o zi, adica iau prima zi de la 00:00. De ex: fara cast,
    // daca caum e ora 19:00, atunci limita devine cu N zile in urma la 19:00. Asta poate exclude comenzi din
    // ziua limita care sunt mai devreme in acea zi
    public List<raport_piesa_reaprovizionare> pieseSubPragCuConsum(int pragStoc, int zile) {

        String sql = """
            SELECT
              p.Denumire,
              p.Stoc,
              ISNULL((
                  SELECT SUM(cp.Cantitate)
                  FROM ComandaPiese cp
                  INNER JOIN Comenzi c ON c.ComandaID = cp.ComandaID
                  WHERE cp.PiesaID = p.PiesaID
                    AND c.DataPrimire >= DATEADD(day, -?, CAST(GETDATE() AS date))
              ), 0) AS FolositeUltimeleZile
            FROM Piese p
            WHERE p.Stoc <= ?
            ORDER BY p.Stoc ASC, FolositeUltimeleZile DESC, p.Denumire ASC
        """;

        // zile este folosit in DATEADD(day, -zile, ...)
        // pragStoc este folosit la filtrul stoc <= prag
        return jdbc.query(sql, (rs, n) -> {

            raport_piesa_reaprovizionare r = new raport_piesa_reaprovizionare();

            r.setDenumire(rs.getString("Denumire"));
            r.setStoc(rs.getInt("Stoc"));
            r.setFolositeUltimeleZile(rs.getInt("FolositeUltimeleZile"));

            return r;
        }, zile, pragStoc);
    }
}
