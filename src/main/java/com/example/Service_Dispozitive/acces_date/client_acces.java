/** Clasa pentru accesul la tabela Clienti din baza de date.
 * aici sunt functiile care creeaza clientul la signup, cauta clientul dupa UserID,
 * salveaza datele de profil si verifica daca profilul minim este complet.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // spune lui spring ca aceasta clasa se ocupa cu accesul la baza de date
public class client_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public client_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // creeaza un rand in tabela Clienti la signup, doar cu UserID
    // restul campurilor pot ramane null (nume, prenume, telefon etc.) pana cand clientul completeaza profilul
    public void creareClientGol(int userId) {
        // insereaza clientul nou legat de contul din tabela Utilizatori
        jdbc.update("INSERT INTO Clienti (UserID) VALUES (?)", userId);
    }

    // cauta un client dupa UserID (userul logat)
    // intoarce Optional:
    // Optional cu client daca exista randul
    // Optional gol daca nu exista
    public Optional<client> findByUserId(int userId) {

        String sql = """
            SELECT ClientID, UserID, Nume, Prenume, Telefon, Email, CNP, Strada, Numar, Oras, Judet
            FROM Clienti
            WHERE UserID = ?
        """;

        // jdbc.query intoarce o lista, chiar daca noi ne asteptam la un singur rand
        // fiecare rand din rezultat este transformat intr-un obiect de tip client
        List<client> rezultat = jdbc.query(sql, (rs, rowNum) -> {

            // creeaza obiectul si ii pune campurile din coloanele din baza de date
            client c = new client();
            c.setClientId(rs.getInt("ClientID"));
            c.setUserId(rs.getInt("UserID"));
            c.setNume(rs.getString("Nume"));
            c.setPrenume(rs.getString("Prenume"));
            c.setTelefon(rs.getString("Telefon"));
            c.setEmail(rs.getString("Email"));
            c.setCnp(rs.getString("CNP"));
            c.setStrada(rs.getString("Strada"));
            c.setNumar(rs.getString("Numar"));
            c.setOras(rs.getString("Oras"));
            c.setJudet(rs.getString("Judet"));

            // returneaza obiectul creat pentru randul curent
            return c;
        }, userId);

        // luam primul element din lista (daca exista) si il intoarcem ca Optional
        return rezultat.stream().findFirst();
    }

    // salveaza datele personale ale clientului in tabela Clienti
    // functioneaza pe baza UserID, nu pe ClientID, pentru ca UserID e ce avem in sesiune la login
    // daca update-ul nu gaseste niciun rand (rows = 0), atunci face insert ca backup
    public void upsertDatePersonale(client c) {

        String updateSql = """
            UPDATE Clienti
            SET Nume = ?, Prenume = ?, Telefon = ?, Email = ?, CNP = ?, Strada = ?, Numar = ?, Oras = ?, Judet = ?
            WHERE UserID = ?
        """;

        // executa update-ul folosind valorile din obiectul client
        // rows = cate randuri au fost modificate (0 sau 1 in mod normal)
        int rows = jdbc.update(updateSql,
                c.getNume(),        // Nume nou
                c.getPrenume(),     // Prenume nou
                c.getTelefon(),     // Telefon nou
                c.getEmail(),       // Email nou
                c.getCnp(),         // CNP nou
                c.getStrada(),      // Strada noua
                c.getNumar(),       // Numar nou
                c.getOras(),        // Oras nou
                c.getJudet(),       // Judet nou
                c.getUserId()       // pe randul cu UserID-ul acestui client
        );

        // daca nu s-a modificat niciun rand, inseamna ca nu exista randul in Clienti pentru acest UserID
        // atunci il cream acum cu insert
        if (rows == 0) {

            String insertSql = """
                INSERT INTO Clienti (UserID, Nume, Prenume, Telefon, Email, CNP, Strada, Numar, Oras, Judet)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            // insereaza randul complet cu toate campurile disponibile
            jdbc.update(insertSql,
                    c.getUserId(),    // legatura cu Utilizatori
                    c.getNume(),
                    c.getPrenume(),
                    c.getTelefon(),
                    c.getEmail(),
                    c.getCnp(),
                    c.getStrada(),
                    c.getNumar(),
                    c.getOras(),
                    c.getJudet()
            );
        }
    }

    // ia ClientID pentru userul logat (UserID)
    // clientId este folosit in alte tabele (Comenzi, Facturi), deci avem nevoie de el
    public Integer getClientIdByUserId(int userId) {
        String sql = "SELECT ClientID FROM Clienti WHERE UserID = ?";
        return jdbc.queryForObject(sql, Integer.class, userId);
    }

    // verifica daca profilul minim este complet pentru userul logat
    // profil minim complet inseamna ca Nume, Prenume si Telefon exista si nu sunt goale
    // returneaza true daca exista un rand care respecta conditiile, altfel false
    public boolean profilMinimComplet(int userId) {

        String sql = """
        SELECT COUNT(*) 
        FROM Clienti
        WHERE UserID = ?
          AND Nume IS NOT NULL AND LTRIM(RTRIM(Nume)) <> ''
          AND Prenume IS NOT NULL AND LTRIM(RTRIM(Prenume)) <> ''
          AND Telefon IS NOT NULL AND LTRIM(RTRIM(Telefon)) <> ''
    """;

        // cnt este numarul de randuri care respecta conditiile (0 sau 1 in mod normal)
        Integer cnt = jdbc.queryForObject(sql, Integer.class, userId);

        // daca cnt > 0 inseamna ca profilul minim este complet
        return cnt != null && cnt > 0;
    }
}
