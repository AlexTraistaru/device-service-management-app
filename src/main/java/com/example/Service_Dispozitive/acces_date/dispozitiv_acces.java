/** Clasa pentru accesul la tabela Dispozitive din baza de date.
 * aici sunt functiile care insereaza un dispozitiv si intorc DispozitivID-ul generat,
 * modifica un dispozitiv, sterge un dispozitiv si modifica campuri din comanda (doar daca are status "trimisa")
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class dispozitiv_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public dispozitiv_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // insereaza un dispozitiv in tabela Dispozitive si intoarce DispozitivID-ul generat de baza de date
    // clientId = clientul care detine dispozitivul
    // departamentId = departamentul dispozitivului (categoria)
    // tipDispozitiv, producator, model, serie = datele dispozitivului completate in formular
    public int insertDispozitiv(int clientId,
                                int departamentId,
                                String tipDispozitiv,
                                String producator,
                                String model,
                                String serie) {

        String sql = """
            INSERT INTO Dispozitive (ClientID, TipDispozitiv, Producator, Model, Serie, DepartamentID)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        // keyHolder tine minte cheia generata automat (DispozitivID)
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // aici facem insert cu PreparedStatement ca sa putem cere cheia generata
        jdbc.update(con -> {

            // Statement.RETURN_GENERATED_KEYS = spune ca vrem sa primim id-ul generat de baza de date
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // punem parametrii in ordinea din VALUES (?, ?, ?, ?, ?, ?)
            ps.setInt(1, clientId);
            ps.setString(2, tipDispozitiv);
            ps.setString(3, producator);
            ps.setString(4, model);
            ps.setString(5, serie);
            ps.setInt(6, departamentId);

            return ps;
        }, keyHolder);

        // luam cheia generata (DispozitivID)
        Number key = keyHolder.getKey();

        // daca key e null inseamna ca insert-ul s-a facut dar nu am primit id-ul generat (ceva e gresit)
        if (key == null) {
            throw new IllegalStateException("nu s-a generat dispozitivID la insert dispozitive");
        }

        // intoarcem id-ul ca int, ca sa il folosim apoi la inserarea comenzii
        return key.intValue();
    }

    // modifica un dispozitiv existent
    // verificarea ca este comanda "Trimisa" se face in service/controller, aici doar executam update-ul
    // clientId = clientul logat (ne asiguram ca modifica doar dispozitivele lui)
    // dispozitivId = dispozitivul modificat
    // tipDispozitiv, producator, model, serie = valorile noi
    // returneaza cate randuri au fost modificate (1 daca a mers, 0 daca nu apartinea clientului)
    public int updateDispozitiv(int clientId,
                                int dispozitivId,
                                String tipDispozitiv,
                                String producator,
                                String model,
                                String serie) {

        String sql = """
        UPDATE Dispozitive
        SET TipDispozitiv = ?, Producator = ?, Model = ?, Serie = ?
        WHERE DispozitivID = ?
          AND ClientID = ?
    """;

        return jdbc.update(sql,
                tipDispozitiv,
                producator,
                model,
                serie,
                dispozitivId,
                clientId
        );
    }

    // sterge un dispozitiv
    // se foloseste dupa ce s-a sters comanda, ca sa nu ramana dispozitivul in baza de date degeaba
    // clientId = clientul logat
    // dispozitivId = dispozitivul de sters
    // returneaza cate randuri au fost sterse (1 daca a mers, 0 daca nu apartinea clientului)
    public int deleteDispozitiv(int clientId, int dispozitivId) {
        String sql = "DELETE FROM Dispozitive WHERE DispozitivID = ? AND ClientID = ?";
        return jdbc.update(sql, dispozitivId, clientId);
    }

    // modifica defectul si metoda de plata din tabela Comenzi, doar daca status este "Trimisa"
    // clientId = clientul logat
    // comandaId = comanda modificata
    // defect = descrierea noua a defectului
    // metodaDePlata = metoda de plata noua
    // returneaza cate randuri au fost modificate (1 daca a mers, 0 daca nu era comanda clientului sau nu era "Trimisa")
    public int updateComandaTrimisa(int clientId, int comandaId, String defect, String metodaDePlata) {

        String sql = """
        UPDATE Comenzi
        SET DefectDispozitiv = ?, MetodaDePlata = ?
        WHERE ComandaID = ?
          AND ClientID = ?
          AND Status = N'Trimisa'
    """;

        return jdbc.update(sql, defect, metodaDePlata, comandaId, clientId);
    }
}
