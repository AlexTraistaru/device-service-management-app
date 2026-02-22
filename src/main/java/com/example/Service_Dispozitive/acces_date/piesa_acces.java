/** Clasa pentru accesul la tabela Piese din baza de date.
 * aici sunt functiile care citesc piesele, cauta o piesa dupa id,
 * scad stocul in siguranta si fac operatii de tip insert, update, delete.
 * mai este si o verificare daca o piesa este deja folosita in comenzi.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.piesa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class piesa_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public piesa_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ia toate piesele din tabela Piese, sortate dupa denumire
    public List<piesa> findAll() {

        String sql = "SELECT PiesaID, Denumire, Cod, Pret, Furnizor, Stoc FROM Piese ORDER BY Denumire ASC";

        // pentru fiecare rand, construim un obiect piesa si ii punem campurile
        return jdbc.query(sql, (rs, rowNum) -> {

            piesa p = new piesa();

            p.setPiesaId(rs.getInt("PiesaID"));
            p.setDenumire(rs.getString("Denumire"));
            p.setCod(rs.getString("Cod"));
            p.setPret(rs.getBigDecimal("Pret"));
            p.setFurnizor(rs.getString("Furnizor"));

            // folosim getObject ca sa putem lua si null daca stocul este null
            // apoi il convertim la Integer
            p.setStoc((Integer) rs.getObject("Stoc"));

            return p;
        });
    }

    // cauta o piesa dupa id
    // piesaId = piesa cautata
    // returneaza piesa sau null daca nu exista
    public piesa findById(int piesaId) {

        String sql = "SELECT PiesaID, Denumire, Cod, Pret, Furnizor, Stoc FROM Piese WHERE PiesaID = ?";

        // rezultatul vine ca lista, dar ar trebui sa fie un singur rand
        return jdbc.query(sql, (rs, rowNum) -> {

            piesa p = new piesa();

            p.setPiesaId(rs.getInt("PiesaID"));
            p.setDenumire(rs.getString("Denumire"));
            p.setCod(rs.getString("Cod"));
            p.setPret(rs.getBigDecimal("Pret"));
            p.setFurnizor(rs.getString("Furnizor"));
            p.setStoc((Integer) rs.getObject("Stoc"));

            return p;

        }, piesaId).stream().findFirst().orElse(null);
    }

    // scade stocul unei piese
    // piesaId = piesa la care scadem
    // cantitate = cat scadem
    // update-ul se face doar daca:
    //  Stoc nu este null
    //  Stoc este mai mare sau egal cu cantitatea ceruta
    // returneaza cate randuri au fost modificate (1 daca a mers, 0 daca nu era stoc suficient)
    public int scadeStoc(int piesaId, int cantitate) {

        String sql = """
            UPDATE Piese
            SET Stoc = Stoc - ?
            WHERE PiesaID = ?
              AND Stoc IS NOT NULL
              AND Stoc >= ?
        """;

        // punem cantitate de doua ori: o data pentru scadere, o data pentru conditia Stoc >= cantitate
        return jdbc.update(sql, cantitate, piesaId, cantitate);
    }

    // insereaza o piesa noua in tabela Piese
    // denumire, cod, pret, furnizor, stoc vin din formularul de adaugare
    public void insert(String denumire, String cod, BigDecimal pret, String furnizor, int stoc) {

        String sql = "INSERT INTO Piese (Denumire, Cod, Pret, Furnizor, Stoc) VALUES (?, ?, ?, ?, ?)";

        jdbc.update(sql, denumire, cod, pret, furnizor, stoc);
    }

    // modifica o piesa existenta in tabela Piese
    // id = PiesaID-ul piesei modificate
    // restul parametrilor sunt valorile noi din formular
    public void update(int id, String denumire, String cod, BigDecimal pret, String furnizor, int stoc) {

        String sql = "UPDATE Piese SET Denumire=?, Cod=?, Pret=?, Furnizor=?, Stoc=? WHERE PiesaID=?";

        jdbc.update(sql, denumire, cod, pret, furnizor, stoc, id);
    }

    // sterge o piesa dupa id
    // id = PiesaID-ul piesei sterse
    public void delete(int id) {

        String sql = "DELETE FROM Piese WHERE PiesaID = ?";

        jdbc.update(sql, id);
    }

    // verifica daca o piesa este folosita in cel putin o comanda
    // id = PiesaID-ul piesei
    // returneaza true daca exista macar un rand in ComandaPiese cu acel PiesaID
    // se foloseste ca sa nu stergem o piesa care deja apare in istoric la comenzi
    public boolean esteFolositaInComenzi(int id) {

        String sql = "SELECT COUNT(*) FROM ComandaPiese WHERE PiesaID = ?";

        Integer c = jdbc.queryForObject(sql, Integer.class, id);

        return c != null && c > 0;
    }
}
