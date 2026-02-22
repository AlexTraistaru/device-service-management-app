/** Clasa pentru modelul de date al unui utilizator.
 * clasa asta tine campurile din tabela Utilizatori
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;
import java.util.Objects;

// reprezinta un utilizator din tabela Utilizatori
public class utilizator {

    // UserID este IDENTITY in db, deci se genereaza automat la insert
    private Integer id;

    // numele de utilizator folosit la login
    private String numeUtilizator;

    // aici salvam hash-ul parolei (nu parola in clar)
    private String parola;

    // email-ul utilizatorului (poate fi folosit la contact)
    private String email;

    // rolul utilizatorului in aplicatie: CLIENT / ANGAJAT
    private String rol;

    // returneaza UserID
    public Integer getId() { return id; }

    // returneaza numele de utilizator
    public String getNumeUtilizator() { return numeUtilizator; }

    // returneaza parola (hash-ul)
    public String getParola() { return parola; }

    // returneaza email-ul
    public String getEmail() { return email; }

    // returneaza rolul
    public String getRol() { return rol; }

    // seteaza UserID (de obicei dupa insert, cand luam id-ul generat)
    public void setId(Integer id) { this.id = id; }

    // seteaza numele de utilizator
    public void setNumeUtilizator(String numeUtilizator) { this.numeUtilizator = numeUtilizator; }

    // seteaza parola (hash-ul)
    public void setParola(String parola) { this.parola = parola; }

    // seteaza email-ul
    public void setEmail(String email) { this.email = email; }

    // seteaza rolul
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // daca compar cu null sau cu un alt tip de obiect nu sunt egale
        utilizator that = (utilizator) o;

        // dacă am id, compar după id; altfel după username
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
            // daca au id comparam dupa id, daca nu, dupa nume de utilizator
        }
        return Objects.equals(this.numeUtilizator, that.numeUtilizator);
    }

    // daca 2 obiecte sunt egale conform equals(), atunci ele trebuie sa aiba acelasi hashcode
    @Override
    public int hashCode() {
        // trebuie să fie consistent cu equals()
        return (id != null) ? Objects.hash(id) : Objects.hash(numeUtilizator);
        // daca exista id, hash ul e calculat din id, altfel, din nume de utilizator
        // Object.hash() combina campurile intr un int
    }

    //citeste campurile curente si le pune intr un string
    @Override
    public String toString() {
        return "utilizator{" +
                "id=" + id +
                ", numeUtilizator='" + numeUtilizator + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
