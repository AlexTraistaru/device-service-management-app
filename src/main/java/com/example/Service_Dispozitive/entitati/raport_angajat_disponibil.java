/** Clasa pentru un rand din raportul "angajatul cel mai disponibil".
 * clasa asta tine nume, prenume, departament si cate comenzi active are angajatul.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

// obiect folosit la afisarea unui rand din raport
public class raport_angajat_disponibil {

    // numele angajatului
    private String nume;

    // prenumele angajatului
    private String prenume;

    // denumirea departamentului
    private String departament;

    // cate comenzi active are (trimisa + in derulare)
    private int comenziActive;

    // returneaza numele
    public String getNume() { return nume; }

    // seteaza numele
    public void setNume(String nume) { this.nume = nume; }

    // returneaza prenumele
    public String getPrenume() { return prenume; }

    // seteaza prenumele
    public void setPrenume(String prenume) { this.prenume = prenume; }

    // returneaza departamentul
    public String getDepartament() { return departament; }

    // seteaza departamentul
    public void setDepartament(String departament) { this.departament = departament; }

    // returneaza numarul de comenzi active
    public int getComenziActive() { return comenziActive; }

    // seteaza numarul de comenzi active
    public void setComenziActive(int comenziActive) { this.comenziActive = comenziActive; }
}
