/** Clasa pentru un serviciu din catalog.
 * clasa asta tine campurile din tabela Servicii si este folosita la afisare si editare servicii.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.math.BigDecimal;

public class serviciu {

    // id-ul serviciului (ServiciuID)
    private Integer serviciuId;

    // denumirea serviciului
    private String denumire;

    // pretul standard al serviciului
    private BigDecimal pretStandard;

    // returneaza ServiciuID
    public Integer getServiciuId() { return serviciuId; }

    // seteaza ServiciuID
    public void setServiciuId(Integer serviciuId) { this.serviciuId = serviciuId; }

    // returneaza denumirea
    public String getDenumire() { return denumire; }

    // seteaza denumirea
    public void setDenumire(String denumire) { this.denumire = denumire; }

    // returneaza pretul standard
    public BigDecimal getPretStandard() { return pretStandard; }

    // seteaza pretul standard
    public void setPretStandard(BigDecimal pretStandard) { this.pretStandard = pretStandard; }
}
