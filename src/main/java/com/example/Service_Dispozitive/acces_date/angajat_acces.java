/** Clasa pentru accesul la tabela Angajati din baza de date.
 * aici sunt functiile care citesc sau modifica date despre angajati folosind sql prin JdbcTemplate.
 * clasa asta este folosita de servicii ca sa nu scriem sql in controller.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository // spune lui spring ca aceasta clasa se ocupa cu accesul la baza de date
public class angajat_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public angajat_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // creeaza un angajat nou in tabela Angajati atunci cand se face un cont de angajat
    // Angajati are DepartamentID obligatoriu, deci alegem primul departament existent si il folosim
    public void creareAngajatGol(int userId) {

        // ia primul DepartamentID din tabela Departament
        Integer depId = jdbc.queryForObject(
                "SELECT TOP 1 DepartamentID FROM Departament ORDER BY DepartamentID ASC",
                Integer.class
        );

        // daca nu exista niciun departament, nu avem ce sa punem in DepartamentID
        // in cazul asta ar fi o problema in baza de date, deci oprim programul cu o eroare
        if (depId == null) {
            throw new IllegalStateException("nu exista niciun departament in tabela Departament");
        }

        // insereaza randul in Angajati
        // userId = id-ul utilizatorului din tabela Utilizatori
        // depId = departamentul ales mai sus
        jdbc.update("INSERT INTO Angajati (UserID, DepartamentID) VALUES (?, ?)", userId, depId);
    }

    // alege angajatul cu cele mai putine comenzi active dintr-un departament
    // comenzi active inseamna status 'Trimisa' sau 'In derulare'
    // departamentId este parametrul variabil
    public Integer alegeAngajatCelMaiDisponibil(int departamentId) {

        String sql = """
                    SELECT TOP 1 a.AngajatID
                    FROM Angajati a
                    LEFT JOIN Comenzi c
                      ON c.AngajatID = a.AngajatID
                     AND c.Status IN (N'Trimisa', N'In derulare')
                    WHERE a.DepartamentID = ?
                    GROUP BY a.AngajatID
                    ORDER BY COUNT(c.ComandaID) ASC, a.AngajatID ASC
                """;

        // jdbc.query intoarce o lista de rezultate
        // (rs, rowNum) citeste coloana AngajatID din fiecare rand
        // luam primul element din lista (top 1) si il returnam
        // daca lista e goala inseamna ca nu exista angajati in departamentul cerut si returnam null
        return jdbc.query(sql, (rs, rowNum) -> rs.getInt("AngajatID"), departamentId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    // ia AngajatID pentru utilizatorul logat (in aplicatie avem UserID, dar in comenzi avem AngajatID)
    // userId = id-ul din tabela Utilizatori
    public Integer getAngajatIdByUserId(int userId) {
        String sql = "SELECT AngajatID FROM Angajati WHERE UserID = ?";
        return jdbc.queryForObject(sql, Integer.class, userId);
    }

    // schimba statusul unei comenzi, dar doar daca comanda este a acelui angajat
    // angajatId = angajatul logat
    // comandaId = comanda care trebuie modificata
    // statusNou = valoarea noua pentru campul Status
    // returneaza cate randuri s-au modificat (1 daca a mers, 0 daca nu era comanda acelui angajat)
    public int updateStatusComanda(int angajatId, int comandaId, String statusNou) {
        String sql = """
        UPDATE Comenzi
        SET Status = ?
        WHERE ComandaID = ?
          AND AngajatID = ?
    """;
        return jdbc.update(sql, statusNou, comandaId, angajatId);
    }

    // ia detaliile angajatului dupa UserID, inclusiv denumirea departamentului
    // intoarce un map cu coloanele selectate:
    // AngajatID, Nume, Prenume, Email, DepartamentID, Denumire (din Departament)
    public java.util.Map<String, Object> getDetaliiAngajatByUserId(int userId) {
        String sql = """
        SELECT a.AngajatID, a.Nume, a.Prenume, a.Email, a.DepartamentID, d.Denumire
        FROM Angajati a
        LEFT JOIN Departament d ON d.DepartamentID = a.DepartamentID
        WHERE a.UserID = ?
    """;
        return jdbc.queryForMap(sql, userId);
    }

    // actualizeaza datele angajatului in tabela Angajati
    // userIdIu = randul din Angajati este identificat prin UserID
    // nume, prenume, email, departamentId vin din formularul de profil
    public void updateDetaliiAngajat(int userId, String nume, String prenume, String email, int departamentId) {
        String sql = """
        UPDATE Angajati
        SET Nume = ?, Prenume = ?, Email = ?, DepartamentID = ?
        WHERE UserID = ?
    """;
        jdbc.update(sql, nume, prenume, email, departamentId, userId);
    }

    // ia DepartamentID al angajatului dupa UserID
    public Integer getDepartamentIdByUserId(int userId) {
        String sql = "SELECT DepartamentID FROM Angajati WHERE UserID = ?";
        return jdbc.queryForObject(sql, Integer.class, userId);
    }
}
