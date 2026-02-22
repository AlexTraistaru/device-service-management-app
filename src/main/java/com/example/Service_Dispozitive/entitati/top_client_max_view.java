/** Clasa pentru rezultatul procesarii unui raport in java.
 * pentru fiecare client tinem:
 * nume complet, pretul maxim al unei facturi si cate facturi are in total (in lista procesata).
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.math.BigDecimal;

public class top_client_max_view {

    // numele complet al clientului
    private String clientNumeComplet;

    // cel mai mare pret de factura pentru acel client
    private BigDecimal maxPretFactura;

    // cate facturi are clientul (in lista procesata)
    private int numarFacturi;

    // constructor gol, util cand cream obiectul si apoi punem campurile cu set
    public top_client_max_view() {}

    // constructor complet, util cand cream obiectul direct cu toate valorile
    public top_client_max_view(String clientNumeComplet, BigDecimal maxPretFactura, int numarFacturi) {
        this.clientNumeComplet = clientNumeComplet;
        this.maxPretFactura = maxPretFactura;
        this.numarFacturi = numarFacturi;
    }

    // returneaza numele complet
    public String getClientNumeComplet() { return clientNumeComplet; }

    // seteaza numele complet
    public void setClientNumeComplet(String clientNumeComplet) { this.clientNumeComplet = clientNumeComplet; }

    // returneaza pretul maxim
    public BigDecimal getMaxPretFactura() { return maxPretFactura; }

    // seteaza pretul maxim
    public void setMaxPretFactura(BigDecimal maxPretFactura) { this.maxPretFactura = maxPretFactura; }

    // returneaza numarul de facturi
    public int getNumarFacturi() { return numarFacturi; }

    // seteaza numarul de facturi
    public void setNumarFacturi(int numarFacturi) { this.numarFacturi = numarFacturi; }
}
