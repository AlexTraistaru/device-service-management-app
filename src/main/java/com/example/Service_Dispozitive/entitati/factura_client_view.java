/** Clasa pentru un rand din raportul cu facturi si clienti.
 * clasa asta tine datele venite din sql pentru raport: nume client + pret factura + data emitere.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.math.BigDecimal;
import java.time.LocalDate;

public class factura_client_view {

    // numele complet al clientului, deja formatat (ex: "popescu ion")
    private String clientNumeComplet;

    // pretul facturii
    private BigDecimal pretFactura;

    // data emiterii facturii
    private LocalDate dataEmitere;

    // returneaza numele complet al clientului
    public String getClientNumeComplet() { return clientNumeComplet; }

    // seteaza numele complet al clientului
    public void setClientNumeComplet(String clientNumeComplet) { this.clientNumeComplet = clientNumeComplet; }

    // returneaza pretul facturii
    public BigDecimal getPretFactura() { return pretFactura; }

    // seteaza pretul facturii
    public void setPretFactura(BigDecimal pretFactura) { this.pretFactura = pretFactura; }

    // returneaza data emiterii
    public LocalDate getDataEmitere() { return dataEmitere; }

    // seteaza data emiterii
    public void setDataEmitere(LocalDate dataEmitere) { this.dataEmitere = dataEmitere; }
}
