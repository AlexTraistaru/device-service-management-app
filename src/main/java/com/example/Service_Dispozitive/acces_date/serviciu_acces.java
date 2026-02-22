/** Clasa pentru accesul la tabela Servicii din baza de date.
 * aici sunt functiile care citesc serviciile, cauta un serviciu dupa id,
 * cauta id-ul unui serviciu dupa denumire exacta,
 * si operatii de tip insert, update, delete
 * mai este si o verificare daca un serviciu este deja folosit in comenzi.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.serviciu;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class serviciu_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public serviciu_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // lista tuturor serviciilor din tabela Servicii (catalog)
    public List<serviciu> findAll() {

        String sql = "SELECT ServiciuID, Denumire, PretStandard FROM Servicii ORDER BY Denumire ASC";

        // pentru fiecare rand, construim un obiect serviciu si ii punem campurile
        return jdbc.query(sql, (rs, rowNum) -> {

            serviciu s = new serviciu();

            s.setServiciuId(rs.getInt("ServiciuID"));
            s.setDenumire(rs.getString("Denumire"));
            s.setPretStandard(rs.getBigDecimal("PretStandard"));

            return s;
        });
    }

    // cauta un serviciu dupa id
    // id = ServiciuID-ul serviciului
    // returneaza serviciu sau null daca nu exista
    public serviciu findById(int id) {

        String sql = "SELECT ServiciuID, Denumire, PretStandard FROM Servicii WHERE ServiciuID = ?";

        // rezultatul vine ca lista, dar ar trebui sa fie un singur rand
        return jdbc.query(sql, (rs, n) -> {

            serviciu s = new serviciu();

            s.setServiciuId(rs.getInt("ServiciuID"));
            s.setDenumire(rs.getString("Denumire"));
            s.setPretStandard(rs.getBigDecimal("PretStandard"));

            return s;
        }, id).stream().findFirst().orElse(null);
    }

    // cauta ServiciuID dupa denumire exacta
    // denumire = textul exact din coloana Denumire
    // se foloseste pentru regula "manopera", ca sa gasim serviciul special dupa nume
    // returneaza id-ul sau null daca nu exista
    public Integer findIdByDenumireExact(String denumire) {

        String sql = "SELECT ServiciuID FROM Servicii WHERE Denumire = ?";

        // ids va contine toate id-urile gasite (normal ar trebui sa fie maxim unul)
        List<Integer> ids = jdbc.query(sql, (rs, n) -> rs.getInt("ServiciuID"), denumire);

        // luam primul daca exista
        return ids.stream().findFirst().orElse(null);
    }

    // insereaza un serviciu nou in tabela Servicii
    // denumire = numele serviciului
    // pret = pretul standard
    public void insert(String denumire, BigDecimal pret) {

        String sql = "INSERT INTO Servicii (Denumire, PretStandard) VALUES (?, ?)";

        jdbc.update(sql, denumire, pret);
    }

    // modifica un serviciu existent in tabela Servicii
    // id = ServiciuID-ul modificat
    // denumire, pret = valorile noi
    public void update(int id, String denumire, BigDecimal pret) {

        String sql = "UPDATE Servicii SET Denumire = ?, PretStandard = ? WHERE ServiciuID = ?";

        jdbc.update(sql, denumire, pret, id);
    }

    // sterge un serviciu dupa id
    // daca serviciul este folosit in ComandaServicii, baza de date poate da eroare (cheie straina)
    public void delete(int id) {

        String sql = "DELETE FROM Servicii WHERE ServiciuID = ?";

        jdbc.update(sql, id);
    }

    // verifica daca un serviciu este folosit in cel putin o comanda
    // id = ServiciuID-ul serviciului
    // returneaza true daca exista macar un rand in ComandaServicii cu acel ServiciuID
    public boolean esteFolositInComenzi(int id) {

        String sql = "SELECT COUNT(*) FROM ComandaServicii WHERE ServiciuID = ?";

        Integer c = jdbc.queryForObject(sql, Integer.class, id);

        return c != null && c > 0;
    }
}
