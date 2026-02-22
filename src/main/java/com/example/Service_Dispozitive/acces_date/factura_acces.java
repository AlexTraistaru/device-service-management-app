/** Clasa pentru accesul la tabela Facturi din baza de date.
 * aici sunt functiile care verifica daca exista factura pentru o comanda,
 * creeaza o factura pe baza unei comenzi, listeaza facturile unui client
 * si listeaza facturi cu nume client intr-un interval de date (raport).
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class factura_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public factura_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // verifica daca exista deja o factura pentru o comanda
    // comandaId = comanda pentru care verificam
    // returneaza true daca exista cel putin un rand in Facturi cu acel ComandaID
    public boolean existaFacturaPentruComanda(int comandaId) {

        String sql = "SELECT COUNT(*) FROM Facturi WHERE ComandaID = ?";

        // c = cate facturi exista pentru comanda respectiva
        Integer c = jdbc.queryForObject(sql, Integer.class, comandaId);

        return c != null && c > 0;
    }

    // creeaza o factura pentru o comanda
    // ia MetodaDePlata direct din comanda, ca sa nu o trimitem separat si sa fie mereu corecta
    // comandaId = comanda pentru care se face factura
    // serie = seria facturii (ex: "AA")
    // numar = numarul facturii
    // dataEmitere = data emiterii facturii
    // pretFactura = pretul final al facturii (calculat in service)
    public void insertFacturaDinComanda(int comandaId, String serie, int numar, LocalDate dataEmitere, BigDecimal pretFactura) {

        String sql = """
            INSERT INTO Facturi (ComandaID, Serie, Numar, DataEmitere, PretFactura, MetodaDePlata)
            SELECT c.ComandaID, ?, ?, ?, ?, c.MetodaDePlata
            FROM Comenzi c
            WHERE c.ComandaID = ?
        """;

        // parametrii se potrivesc cu cele 5 semne ? din sql:
        // serie
        // numar
        // dataEmitere
        // pretFactura
        // comandaId (pentru WHERE)
        jdbc.update(sql, serie, numar, dataEmitere, pretFactura, comandaId);
    }

    // listeaza facturile unui client (pentru pagina clientului)
    // clientId = clientul logat
    // intoarce lista de map-uri ca sa fie usor de afisat in thymeleaf fara un dto separat
    //Interogarea afiseaza toate facturile emise pentru comenzile unui client. Pentru fiecare factura
    // arata seria, numarul, data, pretul, metoda de plata, plus informatii despre dispozitivul din comanda facturata.
    public List<java.util.Map<String, Object>> listFacturiByClientId(int clientId) {

        String sql = """
        SELECT
            f.Serie, f.Numar, f.DataEmitere, f.PretFactura, f.MetodaDePlata,
            c.ComandaID,
            d.TipDispozitiv, d.Producator, d.Model, d.Serie AS SerieDispozitiv
        FROM Facturi f
        INNER JOIN Comenzi c ON c.ComandaID = f.ComandaID
        INNER JOIN Dispozitive d ON d.DispozitivID = c.DispozitivID
        WHERE c.ClientID = ?
        ORDER BY f.DataEmitere DESC, f.FacturaID DESC
    """;

        // queryForList intoarce o lista de randuri, fiecare rand este un Map<coloana, valoare>
        return jdbc.queryForList(sql, clientId);
    }

    // lista facturi + nume client, filtrate intr-un interval de date
    // from = data minima (inclusiv)
    // to = data maxima (inclusiv)
    // se foloseste ca raport
    public List<com.example.Service_Dispozitive.entitati.factura_client_view> listFacturiCuClientInterval(
            java.time.LocalDate from,
            java.time.LocalDate to
    ) {

        String sql = """
        SELECT
            (ISNULL(cl.Nume, '') + ' ' + ISNULL(cl.Prenume, '')) AS ClientNumeComplet,
            f.PretFactura,
            f.DataEmitere
        FROM Facturi f
        INNER JOIN Comenzi c ON c.ComandaID = f.ComandaID
        INNER JOIN Clienti cl ON cl.ClientID = c.ClientID
        WHERE f.DataEmitere >= ? AND f.DataEmitere <= ?
        ORDER BY f.DataEmitere DESC
    """;

        // pentru fiecare rand, construim un factura_client_view si completam campurile
        return jdbc.query(sql, (rs, n) -> {

            com.example.Service_Dispozitive.entitati.factura_client_view v =
                    new com.example.Service_Dispozitive.entitati.factura_client_view();

            // ClientNumeComplet vine din concatenarea nume + prenume
            // trim() ca sa nu ramana spatii la inceput/sfarsit daca unul din ele e null
            v.setClientNumeComplet(rs.getString("ClientNumeComplet").trim());

            v.setPretFactura(rs.getBigDecimal("PretFactura"));
            v.setDataEmitere(rs.getDate("DataEmitere").toLocalDate());

            return v;
        }, from, to);
    }
}
