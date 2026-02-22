/** Clasa pentru accesul la tabela Departament din baza de date.
 * aici sunt functiile care citesc departamentele (de obicei la formulare).
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.departament;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class departament_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public departament_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // intoarce toate departamentele din tabela Departament
    // se foloseste in formulare (ex: categoria dispozitivului sau departamentul angajatului)
    public List<departament> findAll() {

        // ia id-ul si denumirea, sortate alfabetic dupa denumire
        String sql = "SELECT DepartamentID, Denumire FROM Departament ORDER BY Denumire ASC";

        // pentru fiecare rand, construim un obiect departament si ii punem campurile
        return jdbc.query(sql, (rs, rowNum) -> {

            departament d = new departament();

            d.setDepartamentId(rs.getInt("DepartamentID"));
            d.setDenumire(rs.getString("Denumire"));

            return d;
        });
    }
}
