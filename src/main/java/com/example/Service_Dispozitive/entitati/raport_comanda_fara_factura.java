/** Clasa pentru un rand din raportul "comenzi fara factura pana la o data".
 * clasa asta tine data primire, client, departament, tip dispozitiv si status.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.time.LocalDate;

// obiect folosit la afisarea unui rand din raport
public class raport_comanda_fara_factura {

    // data la care a fost primita comanda
    private LocalDate dataPrimire;

    // numele complet al clientului, formatat in sql
    private String client;

    // denumirea departamentului
    private String departament;

    // tipul dispozitivului
    private String tipDispozitiv;

    // statusul comenzii
    private String status;

    // returneaza data primire
    public LocalDate getDataPrimire() { return dataPrimire; }

    // seteaza data primire
    public void setDataPrimire(LocalDate dataPrimire) { this.dataPrimire = dataPrimire; }

    // returneaza clientul
    public String getClient() { return client; }

    // seteaza clientul
    public void setClient(String client) { this.client = client; }

    // returneaza departamentul
    public String getDepartament() { return departament; }

    // seteaza departamentul
    public void setDepartament(String departament) { this.departament = departament; }

    // returneaza tipul dispozitivului
    public String getTipDispozitiv() { return tipDispozitiv; }

    // seteaza tipul dispozitivului
    public void setTipDispozitiv(String tipDispozitiv) { this.tipDispozitiv = tipDispozitiv; }

    // returneaza statusul
    public String getStatus() { return status; }

    // seteaza statusul
    public void setStatus(String status) { this.status = status; }
}
