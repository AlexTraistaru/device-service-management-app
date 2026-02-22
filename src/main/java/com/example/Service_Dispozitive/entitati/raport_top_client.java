/** Clasa pentru un rand din raportul "top clienti dupa total facturat".
 * clasa asta tine nume, prenume, numar facturi si totalul facturat.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.math.BigDecimal;

// obiect folosit la afisarea unui rand din raport
public class raport_top_client {

    // numele clientului
    private String nume;

    // prenumele clientului
    private String prenume;

    // cate facturi are in interval
    private int nrFacturi;

    // suma preturilor facturilor din interval
    private BigDecimal total;

    // returneaza numele
    public String getNume() { return nume; }

    // seteaza numele
    public void setNume(String nume) { this.nume = nume; }

    // returneaza prenumele
    public String getPrenume() { return prenume; }

    // seteaza prenumele
    public void setPrenume(String prenume) { this.prenume = prenume; }

    // returneaza numarul de facturi
    public int getNrFacturi() { return nrFacturi; }

    // seteaza numarul de facturi
    public void setNrFacturi(int nrFacturi) { this.nrFacturi = nrFacturi; }

    // returneaza totalul
    public BigDecimal getTotal() { return total; }

    // seteaza totalul
    public void setTotal(BigDecimal total) { this.total = total; }
}
