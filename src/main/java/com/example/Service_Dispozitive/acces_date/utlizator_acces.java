/** Clasa pentru accesul la tabela Utilizatori din baza de date.
 * aici sunt functiile care cauta un utilizator dupa numeUtilizator
 * si creeaza un utilizator nou la inregistrare
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.acces_date;

import com.example.Service_Dispozitive.entitati.utilizator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class utlizator_acces {

    // jdbcTemplate executa sql (select, insert, update) si intoarce rezultatele
    private final JdbcTemplate jdbc;

    // constructor, spring trimite automat obiectul jdbc
    public utlizator_acces(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // cauta un utilizator dupa numele de utilizator (username)
    // numeUtilizator = textul introdus la login
    // intoarce Optional:
    // - Optional cu utilizator daca exista
    // - Optional gol daca nu exista
    public Optional<utilizator> findByNumeUtilizator(String numeUtilizator) {

        String sql = "SELECT UserID, NumeUtilizator, Parola, Email, Rol " +
                "FROM Utilizatori WHERE NumeUtilizator = ?";

        // rezultatul vine ca lista, chiar daca ne asteptam la maxim un rand
        // fiecare rand este transformat intr-un obiect utilizator
        List<utilizator> rezultat = jdbc.query(sql, (rs, rowNum) -> {

            utilizator u = new utilizator();

            u.setId(rs.getInt("UserID"));
            u.setNumeUtilizator(rs.getString("NumeUtilizator"));
            u.setParola(rs.getString("Parola"));
            u.setEmail(rs.getString("Email"));
            u.setRol(rs.getString("Rol"));

            return u;
        }, numeUtilizator);

        // luam primul rand daca exista si il intoarcem ca Optional
        return rezultat.stream().findFirst();
    }

    // insereaza un utilizator nou in tabela Utilizatori
    // u = obiectul cu datele de signup (numeUtilizator, parola hashuita, email, rol)
    // dupa insert, ia UserID generat de baza de date si il pune in u.setId(...)
    // returneaza acelasi obiect u, dar cu id-ul completat
    public utilizator save(utilizator u) {

        String sql = "INSERT INTO Utilizatori (NumeUtilizator, Parola, Email, Rol) VALUES (?, ?, ?, ?)";

        // keyHolder tine minte cheia generata automat (UserID)
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // folosim PreparedStatement ca sa cerem cheia generata
        jdbc.update(con -> {

            // Statement.RETURN_GENERATED_KEYS = vrem inapoi id-ul generat de baza de date
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // punem parametrii in ordinea din values (?, ?, ?, ?)
            ps.setString(1, u.getNumeUtilizator());
            ps.setString(2, u.getParola());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRol());

            return ps;
        }, keyHolder);

        // luam id-ul generat (UserID)
        Number key = keyHolder.getKey();

        // daca key nu e null, punem id-ul in obiect
        if (key != null) {
            u.setId(key.intValue());
        }

        return u;
    }
}
